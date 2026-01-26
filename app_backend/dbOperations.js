// dbOperations.js
'use strict';

const sql = require('mssql');
const bcrypt = require('bcrypt');
const nodemailer = require('nodemailer');
const Joi = require('joi');
require('dotenv').config();

/* =========================
   Config & Pool (singleton)
   ========================= */
const config = {
  user: process.env.DB_USER,
  password: process.env.DB_PASS,
  server: process.env.DB_SERVER,
  database: process.env.DB_NAME,
  options: {
    encrypt: false,
    trustServerCertificate: true,
    enableArithAbort: true
  },
  pool: {
    max: 20,
    min: 0,
    idleTimeoutMillis: 30000
  },
  connectionTimeout: 15000,
  requestTimeout: 30000
};

let poolPromise = null;

async function getPool() {
  if (poolPromise) return poolPromise;
  poolPromise = (async () => {
    const maxAttempts = 3;
    let attempt = 0;
    while (attempt < maxAttempts) {
      try {
        const p = await sql.connect(config);
        console.log('DB connected');
        return p;
      } catch (err) {
        attempt++;
        console.error(`DB connect attempt ${attempt} failed:`, err.message);
        if (attempt >= maxAttempts) throw err;
        await new Promise(r => setTimeout(r, 1000 * attempt));
      }
    }
  })();
  return poolPromise;
}

function authOptional(req, res, next) {
  const auth = req.headers.authorization

  if (!auth) {
    req.user = null
    return next()
  }

  const token = auth.replace('Bearer ', '')

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET)
    req.user = decoded
  } catch (e) {
    req.user = null
  }

  next()
}

/* =========================
   Mailer (Gmail App Password)
   ========================= */
const MAIL_USER = process.env.EMAIL_USER;
const MAIL_PASS = process.env.EMAIL_PASS;

const mailTransport = nodemailer.createTransport({
  service: 'gmail',
  auth: { user: MAIL_USER, pass: MAIL_PASS }
});

async function sendMailNonBlocking(mailOptions) {
  (async () => {
    const max = 2;
    for (let i = 0; i <= max; i++) {
      try {
        const info = await mailTransport.sendMail(mailOptions);
        console.log('📧 Mail sent to', mailOptions.to, 'id=', info.messageId);
        return;
      } catch (err) {
        console.error('Mail send error:', err && err.message);
        if (i === max) return;
        await new Promise(r => setTimeout(r, 1000 * (i + 1)));
      }
    }
  })();
}

const DEFAULT_AVATAR_PATH = '/uploads/avatars/avatar_default.jpg';

/* =========================
   Validation schemas (Joi)
   ========================= */
const registerSchema = Joi.object({
  username: Joi.string().min(3).max(100).required(),
  email: Joi.string().email().required(),
  password: Joi.string().min(6).required(),
  confirmPassword: Joi.string().valid(Joi.ref('password')).required()
    .messages({ 'any.only': 'Mật khẩu xác nhận không khớp' })
});

async function isPlaylistOwner(playlistId, userId) {
  const pool = await getPool();
  const res = await pool.request()
    .input('playlistId', sql.Int, playlistId)
    .input('userId', sql.Int, userId)
    .query(`
      SELECT 1 FROM playlists
      WHERE id = @playlistId
        AND user_id = @userId
        AND deleted_at IS NULL
    `);
  return res.recordset.length > 0;
}

// Lấy danh sách nghệ sĩ
async function getAllArtists(keyword) {
  try {
    const pool = await getPool();
    const req = pool.request();
    let where = 'WHERE deleted_at IS NULL AND status = 1';
    if (keyword) {
      where += ' AND (name LIKE @keyword OR bio LIKE @keyword)';
      req.input('keyword', sql.NVarChar(255), `%${keyword}%`);
    }
    const res = await req.query(`
      SELECT id, name, bio, image_url, is_verified, slug, created_at
      FROM artists
      ${where}
      ORDER BY created_at DESC
    `);
    return res.recordset;
  } catch (err) {
    console.error('getAllArtists error:', err);
    return [];
  }
}

// Lấy chi tiết nghệ sĩ theo id
async function getArtistById(id) {
  try {
    if (!Number.isInteger(id)) return null;
    const pool = await getPool();
    const res = await pool.request()
      .input('id', sql.Int, id)
      .query(`
        SELECT id, name, bio, image_url, is_verified, slug, created_at
        FROM artists
        WHERE id = @id AND deleted_at IS NULL AND status = 1
      `);
    return res.recordset[0] || null;
  } catch (err) {
    console.error('getArtistById error:', err);
    return null;
  }
}

async function updateAvatar(userId, avatar_url) {
  try {
    const pool = await sql.connect(/* config */);
    await pool.request()
      .input('userId', sql.Int, userId)
      .input('avatar_url', sql.NVarChar, avatar_url)
      .query(`
        UPDATE Users
        SET avatar_url = @avatar_url
        WHERE id = @userId
      `);
    return true;
  } catch (err) {
    console.error('updateAvatar error', err);
    return false;
  }
}

// ---------- ALBUMS ----------
async function getAllAlbums() {
  const pool = await getPool();

  const result = await pool.request().query(`
    SELECT
      a.id,
      a.title,
      a.release_date,
      a.description,
      a.cover_image_url,
      a.created_at,

      ar.id   AS artist_id,
      ar.name AS artist_name

    FROM albums a
    JOIN artists ar ON a.artist_id = ar.id
    WHERE a.deleted_at IS NULL
      AND a.status = 1
    ORDER BY a.release_date DESC
  `);

  return {
    success: true,
    data: result.recordset
  };
}


async function getAlbumById(id) {
  if (!Number.isInteger(id)) return null;
  const pool = await getPool();
  const res = await pool.request()
    .input('id', sql.Int, id)
    .query(`
      SELECT a.id, a.title, a.release_date, a.description, a.cover_image_url,
             a.created_at, ar.id as artist_id, ar.name as artist_name
      FROM albums a
      JOIN artists ar ON a.artist_id = ar.id
      WHERE a.id = @id AND a.deleted_at IS NULL AND a.status = 1
    `);
  return res.recordset[0] || null;
}

// ---------- SEARCH ----------
async function searchAll(keyword, page = 1, limit = 10) {
  if (!keyword) return { success: true, data: { artists: [], albums: [], songs: [] } };
  const pool = await getPool();
  const req = pool.request()
    .input('keyword', sql.NVarChar(255), `%${keyword}%`)
    .input('offset', sql.Int, (page - 1) * limit)
    .input('limit', sql.Int, limit);

  // Artists
  const artistsRes = await req.query(`
    SELECT id, name, bio, image_url, is_verified, slug, created_at
    FROM artists
    WHERE deleted_at IS NULL AND status = 1
      AND (name LIKE @keyword OR bio LIKE @keyword)
    ORDER BY created_at DESC
    OFFSET @offset ROWS FETCH NEXT @limit ROWS ONLY
  `);

  // Albums
  const albumsRes = await req.query(`
    SELECT a.id, a.title, a.release_date, a.cover_image_url, a.description,
           ar.id as artist_id, ar.name as artist_name
    FROM albums a
    JOIN artists ar ON a.artist_id = ar.id
    WHERE a.deleted_at IS NULL AND a.status = 1
      AND (a.title LIKE @keyword OR a.description LIKE @keyword)
    ORDER BY a.release_date DESC
    OFFSET @offset ROWS FETCH NEXT @limit ROWS ONLY
  `);

  // Songs
  const songsRes = await req.query(`
    SELECT s.id, s.title, s.cover_image_url, s.audio_url as src,
           a.id as artist_id, a.name as artist_name
    FROM songs s
    LEFT JOIN artists a ON s.artist_id = a.id
    WHERE s.deleted_at IS NULL AND s.status = 1
      AND (s.title LIKE @keyword OR a.name LIKE @keyword)
    ORDER BY s.created_at DESC
    OFFSET @offset ROWS FETCH NEXT @limit ROWS ONLY
  `);

  return {
    success: true,
    data: {
      artists: artistsRes.recordset,
      albums: albumsRes.recordset,
      songs: songsRes.recordset
    }
  };
}

function generateOTP() {
  return Math.floor(100000 + Math.random() * 900000).toString();
}

//mapper Song cho recommend song va top songs
function mapSong(row) {
  return {
    id: row.id,
    title: row.title,
    duration_seconds: row.duration_seconds,
    audio_url: row.audio_url,
    cover_image_url: row.cover_image_url,
    view_count: Number(row.view_count),
    slug: row.slug,
    created_at: row.created_at,

    artist: {
      id: row.artist_id,
      name: row.artist_name,
      image_url: row.artist_image_url,
      is_verified: Boolean(row.is_verified),
      slug: row.artist_slug
    },

    album: row.album_id ? {
      id: row.album_id,
      title: row.album_title,
      cover_image_url: row.album_cover_image_url,
      release_date: row.release_date
    } : null,

    genre: row.genre_id ? {
      id: row.genre_id,
      name: row.genre_name,
      slug: row.genre_slug
    } : null
  };
}

// Lấy Top 10 bài hát nhiều view
async function getTopSongs(limit = 10) {
  const pool = await getPool();
  const result = await pool.request().query(`
    SELECT TOP (@limit)
      s.id, s.title, s.duration_seconds, s.audio_url,
      s.cover_image_url, s.view_count, s.slug, s.created_at,

      a.id AS artist_id, a.name AS artist_name,
      a.image_url AS artist_image_url, a.is_verified, a.slug AS artist_slug,

      al.id AS album_id, al.title AS album_title,
      al.cover_image_url AS album_cover_image_url, al.release_date,

      g.id AS genre_id, g.name AS genre_name, g.slug AS genre_slug
    FROM songs s
    JOIN artists a ON s.artist_id = a.id
    LEFT JOIN albums al ON s.album_id = al.id
    LEFT JOIN genres g ON s.genre_id = g.id
    WHERE s.status = 1
    ORDER BY s.view_count DESC, s.created_at DESC
  `);

  return result.recordset.map(mapSong);
}

async function getRecommendSongsForGuest(limit = 10) {
  const pool = await getPool();

  const result = await pool.request()
    .input('limit', sql.Int, limit)
    .query(`
      WITH TopArtists AS (
        SELECT TOP 10 artist_id
        FROM songs
        WHERE status = 1
        GROUP BY artist_id
        ORDER BY SUM(view_count) DESC
      )
      SELECT TOP (@limit)
        s.id, s.title, s.duration_seconds, s.audio_url, s.cover_image_url,
        s.view_count, s.slug, s.created_at,

        a.id AS artist_id, a.name AS artist_name,
        a.image_url AS artist_image_url, a.is_verified, a.slug AS artist_slug,

        al.id AS album_id, al.title AS album_title,
        al.cover_image_url AS album_cover_image_url, al.release_date,

        g.id AS genre_id, g.name AS genre_name, g.slug AS genre_slug
      FROM songs s
      JOIN artists a ON s.artist_id = a.id
      LEFT JOIN albums al ON s.album_id = al.id
      LEFT JOIN genres g ON s.genre_id = g.id
      WHERE s.status = 1
        AND s.artist_id IN (SELECT artist_id FROM TopArtists)
      ORDER BY NEWID();
    `);

  return result.recordset.map(mapSong);
}


// Hàm gợi ý bài hát cho user
async function getRecommendSongsForUser(userId, limit = 10) {
  const pool = await getPool();

  //Lấy bài nghe gần nhất
  const recent = await pool.request()
    .input('userId', sql.Int, userId)
    .query(`
      SELECT TOP 1 s.id AS song_id, s.artist_id, s.genre_id
      FROM histories h
      JOIN songs s ON h.song_id = s.id
      WHERE h.user_id = @userId
      ORDER BY h.played_at DESC
    `);

  //Nếu user chưa có history
  if (recent.recordset.length === 0) {
    return getRecommendSongsForGuest(limit);
  }

  const { song_id, artist_id, genre_id } = recent.recordset[0];

  //Recommend dựa trên artist / genre
  const result = await pool.request()
    .input('artistId', sql.Int, artist_id)
    .input('genreId', sql.Int, genre_id)
    .input('songId', sql.Int, song_id)
    .input('limit', sql.Int, limit)
    .query(`
      SELECT TOP (@limit)
        s.id, s.title, s.duration_seconds, s.audio_url, s.cover_image_url,
        s.view_count, s.slug, s.created_at,

        a.id AS artist_id, a.name AS artist_name,
        a.image_url AS artist_image_url, a.is_verified, a.slug AS artist_slug,

        al.id AS album_id, al.title AS album_title,
        al.cover_image_url AS album_cover_image_url, al.release_date,

        g.id AS genre_id, g.name AS genre_name, g.slug AS genre_slug
      FROM songs s
      JOIN artists a ON s.artist_id = a.id
      LEFT JOIN albums al ON s.album_id = al.id
      LEFT JOIN genres g ON s.genre_id = g.id
      WHERE s.status = 1
        AND s.id <> @songId
        AND (
          s.artist_id = @artistId
          OR s.genre_id = @genreId
        )
      ORDER BY NEWID();
    `);

  return result.recordset.map(mapSong);
}

/* =========================
   Exported DB operations
   ========================= */
module.exports = {
  // ---------- RECOMMENDING----------
  getTopSongs, getRecommendSongsForGuest, getRecommendSongsForUser,

  // ---------- ALBUMS ----------
  getAllAlbums,
  getAlbumById,

  // ---------- SEARCH ----------
  searchAll,
  
  // ---------- ARTISTS ----------
  getAllArtists,
  getArtistById,

  // ---------- AUTH ----------
  registerUser: async (username, email, password, confirmPassword, req) => {
    const DEFAULT_AVATAR_PATH = '/uploads/avatars/avatar_default.jpg';
    let avatar_url = DEFAULT_AVATAR_PATH;

    if (req) {
      avatar_url = `${req.protocol}://${req.get('host')}${DEFAULT_AVATAR_PATH}`;
    }

    // validate cơ bản
    const { error } = registerSchema.validate(
      { username, email, password, confirmPassword },
      { abortEarly: false }
    );
    if (error) {
      console.error('Joi validation failed:', error.details);
      return { success: false, message: 'Validation failed', details: error.details };
    }

    // kiểm tra confirm password
    if (password !== confirmPassword) {
      return { success: false, message: 'Mật khẩu xác nhận không khớp' };
    }

    const pool = await getPool();
    const transaction = new sql.Transaction(pool);
    try {
      await transaction.begin();

      // check username
      const chkUser = await transaction.request()
        .input('username', sql.NVarChar(100), username)
        .query('SELECT id FROM users WHERE username = @username AND deleted_at IS NULL');
      if (chkUser.recordset.length) {
        await transaction.rollback();
        return { success: false, message: 'Username đã tồn tại' };
      }

      // check email
      const chkEmail = await transaction.request()
        .input('email', sql.NVarChar(255), email)
        .query('SELECT id FROM users WHERE email = @email AND deleted_at IS NULL');
      if (chkEmail.recordset.length) {
        await transaction.rollback();
        return { success: false, message: 'Email đã được sử dụng' };
      }

      const password_hash = await bcrypt.hash(password, 12);

      // insert vào bảng users
      const insertUser = await transaction.request()
        .input('username', sql.NVarChar(100), username)
        .input('email', sql.NVarChar(255), email)
        .input('password_hash', sql.NVarChar(255), password_hash)
        .input('avatar_url', sql.NVarChar(500), avatar_url)
        .query(`
          INSERT INTO users (username, email, password_hash, avatar_url, role, status)
          OUTPUT INSERTED.id, INSERTED.username, INSERTED.email, INSERTED.avatar_url, INSERTED.role, INSERTED.created_at
          VALUES (@username, @email, @password_hash, @avatar_url, 'user', 1)
        `);

      const user = insertUser.recordset[0];

      // insert vào bảng settings (default values)
      await transaction.request()
        .input('user_id', sql.Int, user.id)
        .query(`INSERT INTO settings (user_id) VALUES (@user_id)`);

      await transaction.commit();

      const otp = generateOTP();
      sendMailNonBlocking({
        from: `"Music App" <${MAIL_USER}>`,
        to: user.email,
        subject: 'Chào mừng đến với Music App',
        html: `<p>Xin chào ${username}<br/>Mã OTP của bạn là: ${otp}</p>`
      });

      return { success: true, message: 'Đăng ký thành công', user };
    } catch (err) {
      try { await transaction.rollback(); } catch (_) {}
      console.error('registerUser error:', err);
      throw err;
    }
  },

  loginUser: async (username, password) => {
    if (!username || !password) return { success: false, message: 'Thiếu thông tin đăng nhập' };
    const pool = await getPool();
    try {
      const res = await pool.request()
        .input('username', sql.NVarChar(100), username)
        .query(`
          SELECT u.id, u.username, u.email, u.password_hash, u.avatar_url, u.role, u.status,
                 p.full_name, p.date_of_birth, p.gender, p.phone, p.address, p.bio
          FROM users u
          LEFT JOIN profiles p ON u.id = p.user_id
          WHERE u.username = @username AND u.deleted_at IS NULL
        `);

      if (!res.recordset.length) return { success: false, message: 'Username không tồn tại' };

      const user = res.recordset[0];
      if (!user.status) return { success: false, message: 'Tài khoản đã bị khóa' };

      const ok = await bcrypt.compare(password, user.password_hash);
      if (!ok) return { success: false, message: 'Sai mật khẩu' };

      await pool.request()
        .input('userId', sql.Int, user.id)
        .query('UPDATE users SET last_login = SYSDATETIME() WHERE id = @userId');

      delete user.password_hash;
      return { success: true, message: 'Đăng nhập thành công', user };
    } catch (err) {
      console.error('loginUser error:', err);
      throw err;
    }
  },

  findUser: async (username) => {
    if (!username) return null;
    const pool = await getPool();
    const res = await pool.request()
      .input('username', sql.NVarChar(100), username)
      .query(`
        SELECT u.id, u.username, u.role, u.email, u.avatar_url,
       p.full_name, p.date_of_birth, p.gender, p.phone
        FROM users u
        LEFT JOIN profiles p ON u.id = p.user_id
        WHERE u.username = @username AND u.deleted_at IS NULL
      `);
    return res.recordset[0] || null;
  },

  // ---------- PROFILE ----------
  getProfile: async (userId) => {
    if (!Number.isInteger(userId)) return null;
    const pool = await getPool();
    const res = await pool.request()
      .input('userId', sql.Int, userId)
      .query(`
        SELECT p.*, u.username, u.email, u.avatar_url, u.role
        FROM profiles p
        JOIN users u ON p.user_id = u.id
        WHERE p.user_id = @userId
      `);
    return res.recordset[0] || null;
  },

  updateProfile: async (userId, data) => {
    if (!Number.isInteger(userId)) return { success: false, message: 'Invalid user ID' };
    const pool = await getPool();
    try {
      const fields = [];
      const req = pool.request().input('userId', sql.Int, userId);

      if (data.full_name !== undefined) {
        fields.push('full_name = @full_name');
        req.input('full_name', sql.NVarChar(255), data.full_name);
      }
      if (data.date_of_birth !== undefined) {
        fields.push('date_of_birth = @date_of_birth');
        req.input('date_of_birth', sql.Date, data.date_of_birth);
      }
      if (data.gender !== undefined) {
        fields.push('gender = @gender');
        req.input('gender', sql.NVarChar(20), data.gender);
      }
      if (data.phone !== undefined) {
        fields.push('phone = @phone');
        req.input('phone', sql.NVarChar(20), data.phone);
      }
      if (data.address !== undefined) {
        fields.push('address = @address');
        req.input('address', sql.NVarChar(255), data.address);
      }
      if (data.bio !== undefined) {
        fields.push('bio = @bio');
        req.input('bio', sql.NVarChar(sql.MAX), data.bio);
      }

      if (fields.length === 0) return { success: false, message: 'No fields to update' };

      fields.push('updated_at = SYSDATETIME()');

      await req.query(`UPDATE profiles SET ${fields.join(', ')} WHERE user_id = @userId`);

      return { success: true, message: 'Profile updated successfully' };
    } catch (err) {
      console.error('updateProfile error:', err);
      throw err;
    }
  },

  // ---------- SETTINGS ----------
  getSettings: async (userId) => {
    if (!Number.isInteger(userId)) return null;
    const pool = await getPool();
    const res = await pool.request()
      .input('userId', sql.Int, userId)
      .query('SELECT * FROM settings WHERE user_id = @userId');
    return res.recordset[0] || null;
  },

  updateSettings: async (userId, data) => {
    if (!Number.isInteger(userId)) return { success: false, message: 'Invalid user ID' };
    const pool = await getPool();
    try {
      const fields = [];
      const req = pool.request().input('userId', sql.Int, userId);

      if (data.theme !== undefined) {
        fields.push('theme = @theme');
        req.input('theme', sql.NVarChar(20), data.theme);
      }
      if (data.language !== undefined) {
        fields.push('language = @language');
        req.input('language', sql.NVarChar(10), data.language);
      }
      if (data.notification_enabled !== undefined) {
        fields.push('notification_enabled = @notification_enabled');
        req.input('notification_enabled', sql.Bit, data.notification_enabled ? 1 : 0);
      }
      if (data.download_quality !== undefined) {
        fields.push('download_quality = @download_quality');
        req.input('download_quality', sql.NVarChar(20), data.download_quality);
      }
      if (data.autoplay_next !== undefined) {
        fields.push('autoplay_next = @autoplay_next');
        req.input('autoplay_next', sql.Bit, data.autoplay_next ? 1 : 0);
      }
      if (data.explicit_filter !== undefined) {
        fields.push('explicit_filter = @explicit_filter');
        req.input('explicit_filter', sql.Bit, data.explicit_filter ? 1 : 0);
      }

      if (fields.length === 0) return { success: false, message: 'No fields to update' };

      fields.push('updated_at = SYSDATETIME()');

      await req.query(`UPDATE settings SET ${fields.join(', ')} WHERE user_id = @userId`);

      return { success: true, message: 'Settings updated successfully' };
    } catch (err) {
      console.error('updateSettings error:', err);
      throw err;
    }
  },

  // ---------- SONGS ----------
  getAllSongs: async () => {
  const pool = await getPool();

  const result = await pool.request().query(`
    SELECT
      s.id,
      s.title,
      s.duration_seconds,
      s.audio_url,
      s.cover_image_url,
      s.view_count,
      s.slug,
      s.created_at,

      a.id   AS artist_id,
      a.name AS artist_name,

      g.id   AS genre_id,
      g.name AS genre_name

    FROM songs s
    LEFT JOIN artists a ON s.artist_id = a.id
    LEFT JOIN genres g ON s.genre_id = g.id
    WHERE s.deleted_at IS NULL
      AND s.status = 1
    ORDER BY s.created_at DESC
  `);

  return {
    success: true,
    data: result.recordset
  };
},

  getSongById: async (id) => {
    if (!Number.isInteger(id)) return null;
    const pool = await getPool();
    await pool.request().input('id', sql.Int, id).query('UPDATE songs SET view_count = view_count + 1 WHERE id = @id');
    const res = await pool.request()
      .input('id', sql.Int, id)
      .query(`
        SELECT s.id, s.title, s.audio_url as src, s.duration_seconds as duration,
               s.cover_image_url, s.lyrics_content, s.view_count, s.slug,
               a.id as artist_id, a.name as artist_name, a.image_url as artist_image,
               g.id as genre_id, g.name as genre_name,
               al.id as album_id, al.title as album_title, al.cover_image_url as album_cover
        FROM songs s
        LEFT JOIN artists a ON s.artist_id = a.id
        LEFT JOIN genres g ON s.genre_id = g.id
        LEFT JOIN albums al ON s.album_id = al.id
        WHERE s.id = @id AND s.deleted_at IS NULL
      `);
    return res.recordset[0] || null;
  },

  // ---------- PLAYLISTS ----------
  getPlaylistsByUser: async (userId) => {
    if (!Number.isInteger(userId)) return [];
    const pool = await getPool();
    const res = await pool.request()
      .input('userId', sql.Int, userId)
      .query(`
        SELECT p.id, p.name, p.description, p.is_public, p.thumbnail_url, p.created_at, p.updated_at,
               COUNT(ps.song_id) as song_count
        FROM playlists p
        LEFT JOIN playlist_songs ps ON p.id = ps.playlist_id
        WHERE p.user_id = @userId AND p.deleted_at IS NULL
        GROUP BY p.id, p.name, p.description, p.is_public, p.thumbnail_url, p.created_at, p.updated_at
        ORDER BY p.created_at DESC
      `);
    return res.recordset;
  },

  getPlaylistDetail: async (playlistId) => {
    if (!Number.isInteger(playlistId)) return null;
    const pool = await getPool();
    const pRes = await pool.request()
      .input('id', sql.Int, playlistId)
      .query(`
        SELECT p.*, u.username, pr.full_name, COUNT(ps.song_id) as song_count
        FROM playlists p
        LEFT JOIN users u ON p.user_id = u.id
        LEFT JOIN profiles pr ON u.id = pr.user_id
        LEFT JOIN playlist_songs ps ON p.id = ps.playlist_id
        WHERE p.id = @id AND p.deleted_at IS NULL
        GROUP BY p.id, p.user_id, p.name, p.description, p.is_public, p.thumbnail_url, p.created_at, p.updated_at, p.deleted_at, u.username, pr.full_name
      `);
    if (!pRes.recordset.length) return null;
    const playlist = pRes.recordset[0];
    const songsRes = await pool.request()
      .input('playlistId', sql.Int, playlistId)
      .query(`
        SELECT s.id, s.title, s.duration_seconds as duration, s.audio_url as src, s.cover_image_url, a.name as artist_name, a.id as artist_id, ps.order_index, ps.added_at
        FROM playlist_songs ps
        JOIN songs s ON ps.song_id = s.id
        LEFT JOIN artists a ON s.artist_id = a.id
        WHERE ps.playlist_id = @playlistId
        ORDER BY ps.order_index
      `);
    playlist.songs = songsRes.recordset;
    return playlist;
  },

  createPlaylist: async (userId, name, description = null, isPublic = false) => {
    if (!Number.isInteger(userId) || !name) throw new Error('Invalid input');
    const pool = await getPool();
    const res = await pool.request()
      .input('userId', sql.Int, userId)
      .input('name', sql.NVarChar(255), name)
      .input('description', sql.NVarChar(255), description)
      .input('isPublic', sql.Bit, isPublic ? 1 : 0)
      .query(`
        INSERT INTO playlists (user_id, name, description, is_public)
        OUTPUT INSERTED.*
        VALUES (@userId, @name, @description, @isPublic)
      `);
    return res.recordset[0];
  },

  deletePlaylist: async (playlistId, userId) => {
    if (!Number.isInteger(playlistId)) return false;
    if (!Number.isInteger(userId)) return false;

    // check ownership
    if (!(await isPlaylistOwner(playlistId, userId))) return false;

    const pool = await getPool();
    const res = await pool.request()
      .input('id', sql.Int, playlistId)
      .query(`
        UPDATE playlists
        SET deleted_at = SYSDATETIME()
        WHERE id = @id AND deleted_at IS NULL
      `);

    return res.rowsAffected[0] > 0;
  },

  addSongToPlaylist: async (playlistId, songId, userId) => {
    if (!Number.isInteger(playlistId) || !Number.isInteger(songId)) {
      return { success: false, message: 'Invalid input' };
    }

    // check owner
    const isOwner = await isPlaylistOwner(playlistId, userId);
    if (!isOwner) {
      return { success: false, message: 'Forbidden' };
    }

    const pool = await getPool();

    try {
      // check song exists in playlist
      const chk = await pool.request()
        .input('playlistId', sql.Int, playlistId)
        .input('songId', sql.Int, songId)
        .query(`
          SELECT id 
          FROM playlist_songs 
          WHERE playlist_id = @playlistId AND song_id = @songId
        `);

      if (chk.recordset.length) {
        return { success: false, message: 'Bài hát đã có trong playlist' };
      }

      // insert with order
      await pool.request()
        .input('playlistId', sql.Int, playlistId)
        .input('songId', sql.Int, songId)
        .query(`
          DECLARE @maxOrder INT;
          SELECT @maxOrder = ISNULL(MAX(order_index), 0)
          FROM playlist_songs
          WHERE playlist_id = @playlistId;

          INSERT INTO playlist_songs (playlist_id, song_id, order_index)
          VALUES (@playlistId, @songId, @maxOrder + 1);
        `);

      return { success: true, message: 'Đã thêm bài hát vào playlist' };
    } catch (err) {
      console.error('addSongToPlaylist error:', err);
      return { success: false, message: 'Lỗi khi thêm bài hát' };
    }
  },

  removeSongFromPlaylist: async (playlistId, songId, userId) => { 
    if (!Number.isInteger(playlistId) || !Number.isInteger(songId)) return false;

    const isOwner = await isPlaylistOwner(playlistId, userId);
    if (!isOwner) return false;

    const pool = await getPool();
    const res = await pool.request()
      .input('playlistId', sql.Int, playlistId)
      .input('songId', sql.Int, songId)
      .query(`
        DELETE FROM playlist_songs
        WHERE playlist_id = @playlistId AND song_id = @songId
      `);

    return res.rowsAffected[0] > 0;
  },

  // ---------- GENRES ----------
  getAllGenres: async() => {
    try {
      const pool = await getPool();
      const res = await pool.request().query(`
        SELECT id, name, slug, description, created_at
        FROM genres
        ORDER BY name;
      `);
      return res.recordset;
    } catch (err) {
      console.error('getAllGenres error:', err);
      return [];
    }
  },

  getGenreById: async (id) => {
    if (!Number.isInteger(id)) return null;
    try {
      const pool = await getPool();
      const res = await pool.request()
        .input('id', sql.Int, id)
        .query(`
          SELECT id, name, slug, description, created_at
          FROM genres
          WHERE id = @id;
        `);
      return res.recordset[0] || null;
    } catch (err) {
      console.error('getGenreById error:', err);
      return null;
    }
  },

  // ---------- FAVORITES ----------
  getFavorites: async (userId) => {
    if (!Number.isInteger(userId)) return [];
    const pool = await getPool();
    const res = await pool.request()
      .input('userId', sql.Int, userId)
      .query(`
        SELECT s.id, s.title, s.audio_url as src, s.duration_seconds as duration, s.cover_image_url, a.name as artist_name, a.id as artist_id, f.favorited_at
        FROM favorites f
        JOIN songs s ON f.song_id = s.id
        LEFT JOIN artists a ON s.artist_id = a.id
        WHERE f.user_id = @userId AND f.deleted_at IS NULL
        ORDER BY f.favorited_at DESC
      `);
    return res.recordset;
  },

  addFavorite: async (userId, songId) => {
    if (!Number.isInteger(userId) || !Number.isInteger(songId)) return { success: false, message: 'Invalid input' };
    const pool = await getPool();
    try {
      const chk = await pool.request()
        .input('userId', sql.Int, userId)
        .input('songId', sql.Int, songId)
        .query('SELECT id FROM favorites WHERE user_id = @userId AND song_id = @songId AND deleted_at IS NULL');

      if (chk.recordset.length) return { success: false, message: 'Bài hát đã có trong yêu thích' };

      const deleted = await pool.request()
        .input('userId', sql.Int, userId)
        .input('songId', sql.Int, songId)
        .query('SELECT id FROM favorites WHERE user_id = @userId AND song_id = @songId AND deleted_at IS NOT NULL');

      if (deleted.recordset.length) {
        await pool.request()
          .input('userId', sql.Int, userId)
          .input('songId', sql.Int, songId)
          .query('UPDATE favorites SET deleted_at = NULL, favorited_at = SYSDATETIME() WHERE user_id = @userId AND song_id = @songId');
      } else {
        await pool.request()
          .input('userId', sql.Int, userId)
          .input('songId', sql.Int, songId)
          .query('INSERT INTO favorites (user_id, song_id) VALUES (@userId, @songId)');
      }
      return { success: true, message: 'Đã thêm vào yêu thích' };
    } catch (err) {
      console.error('addFavorite error:', err);
      return { success: false, message: 'Lỗi khi thêm vào yêu thích' };
    }
  },

  removeFavorite: async (userId, songId) => {
    if (!Number.isInteger(userId) || !Number.isInteger(songId)) return false;
    const pool = await getPool();
    const res = await pool.request()
      .input('userId', sql.Int, userId)
      .input('songId', sql.Int, songId)
      .query('UPDATE favorites SET deleted_at = SYSDATETIME() WHERE user_id = @userId AND song_id = @songId AND deleted_at IS NULL');
    return res.rowsAffected[0] > 0;
  },

  // ---------- HISTORY ----------
  getHistories: async (userId, limit = 50) => {
    if (!Number.isInteger(userId)) return [];
    const pool = await getPool();
    const res = await pool.request()
      .input('userId', sql.Int, userId)
      .input('limit', sql.Int, Math.min(limit, 200))
      .query(`
        SELECT TOP (@limit) h.id, h.played_at, h.duration_played, s.id as song_id, s.title, s.audio_url as src, s.duration_seconds as duration, s.cover_image_url, a.name as artist_name, a.id as artist_id
        FROM histories h
        JOIN songs s ON h.song_id = s.id
        LEFT JOIN artists a ON s.artist_id = a.id
        WHERE h.user_id = @userId
        ORDER BY h.played_at DESC
      `);
    return res.recordset;
  },

  addHistory: async (userId, songId, duration_played = 0) => {
    if (!Number.isInteger(userId) || !Number.isInteger(songId)) return false;
    const pool = await getPool();
    try {
      await pool.request()
        .input('userId', sql.Int, userId)
        .input('songId', sql.Int, songId)
        .input('duration_played', sql.Int, duration_played)
        .query('INSERT INTO histories (user_id, song_id, duration_played) VALUES (@userId, @songId, @duration_played)');
      return true;
    } catch (err) {
      console.error('addHistory error:', err);
      return false;
    }
  },
  updateAvatar,
};

/* Graceful shutdown */
process.on('SIGINT', async () => {
  try {
    if (poolPromise) {
      const p = await poolPromise;
      await p.close();
      console.log('✅ DB connection closed');
    }
  } catch (err) {
    console.error('Error closing DB pool:', err);
  } finally {
    process.exit(0);
  }
});