// server.js
'use strict';
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const rateLimit = require('express-rate-limit');
const { body, param, query, validationResult } = require('express-validator');
const jwt = require('jsonwebtoken');
const db = require('./dbOperations');
const YAML = require('yamljs');
const path = require('path');
const swaggerUi = require('swagger-ui-express');
const swaggerJsdoc = require('swagger-jsdoc');
const multer = require('multer');
require('dotenv').config();
const fs = require('fs');

const app = express();
const PORT = process.env.PORT || 5000;
if (!process.env.JWT_SECRET) {
  throw new Error('JWT_SECRET is not defined');
}
const JWT_SECRET = process.env.JWT_SECRET
const NODE_ENV = process.env.NODE_ENV || 'production';

app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cors({
  origin: '*', // hoặc ['http://localhost:3000'] nếu muốn giới hạn
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));
app.use(helmet());
app.use(morgan('combined'));

/* -------------------------
   Middlewares and security
   ------------------------- */

const swaggerDocument = YAML.load(path.join(__dirname, 'openapi.yaml'));
const basicAuth = (req, res, next) => {
  const auth = { user: process.env.SWAGGER_USER, pass: process.env.SWAGGER_PASS };
  const b64 = (req.headers.authorization || '').split(' ')[1] || '';
  const [user, pass] = Buffer.from(b64, 'base64').toString().split(':');
  if (user === auth.user && pass === auth.pass) return next();
  res.set('WWW-Authenticate', 'Basic realm="Docs"');
  return res.status(401).send('Authentication required.');
};
app.use('/api-docs',/* basicAuth,*/ swaggerUi.serve, swaggerUi.setup(swaggerDocument));

const apiLimiter = rateLimit({
  windowMs: 60 * 1000,
  max: 120,
  standardHeaders: true,
  legacyHeaders: false
});
app.use('/api/', apiLimiter);

app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

/* -------------------------
   Play audio route
   ------------------------- */

const SONGS_DIR = path.join(__dirname, 'uploads', 'songs');

//http://localhost:5000/play/noi-nay-co-anh.mp3 để chạy
app.get('/play/:filename', (req, res) => {
  const fileName = req.params.filename;
  const filePath = path.join(SONGS_DIR, fileName);

  if (!fs.existsSync(filePath)) {
    return res.status(404).json({ success: false, message: 'Not found' });
  }

  // lấy kích thước file
  const stat = fs.statSync(filePath);
  const fileSize = stat.size;
  const range = req.headers.range;

  if (range) {
    // partial stream cho tua
    const parts = range.replace(/bytes=/, "").split("-");
    const start = parseInt(parts[0], 10);
    const end = parts[1] ? parseInt(parts[1], 10) : fileSize - 1;
    const chunkSize = (end - start) + 1;
    const file = fs.createReadStream(filePath, { start, end });
    const head = {
      'Content-Range': `bytes ${start}-${end}/${fileSize}`,
      'Accept-Ranges': 'bytes',
      'Content-Length': chunkSize,
      'Content-Type': 'audio/mpeg',
    };
    res.writeHead(206, head);
    file.pipe(res);
  } else {
    // full stream
    res.writeHead(200, {
      'Content-Length': fileSize,
      'Content-Type': 'audio/mpeg',
    });
    fs.createReadStream(filePath).pipe(res);
  }
});


/* -------------------------
   Helpers
   ------------------------- */

const asyncHandler = fn => (req, res, next) => Promise.resolve(fn(req, res, next)).catch(next);

const validate = validations => async (req, res, next) => {
  await Promise.all(validations.map(v => v.run(req)));
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ success: false, message: 'Validation error', errors: errors.array() });
  }
  next();
};

const authenticateToken = (req, res, next) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (!token) return res.status(401).json({ success: false, message: 'Không tìm thấy token xác thực' });
  jwt.verify(token, JWT_SECRET, (err, payload) => {
    if (err) return res.status(403).json({ success: false, message: 'Token không hợp lệ hoặc đã hết hạn' });
    req.user = payload;
    next();
  });
};

const requireRole = role => (req, res, next) => {
  if (!req.user) return res.status(401).json({ success: false, message: 'Unauthorized' });
  if (req.user.role !== role) return res.status(403).json({ success: false, message: 'Forbidden' });
  next();
};

/* -------------------------
   AUTH Routes
   ------------------------- */

app.post('/api/register',
  validate([
    body('username').isString().trim().isLength({ min: 3, max: 100 }),
    body('email').isEmail().normalizeEmail(),
    body('password').isString().isLength({ min: 6 }),
    body('full_name').isString().trim().isLength({ min: 1, max: 255 })
  ]),
  asyncHandler(async (req, res) => {
    console.log('--- /api/register called ---');
    const { username, email, password, full_name} = req.body;
    const result = await db.registerUser(
      username.trim(),
      email.trim().toLowerCase(),
      password,
      full_name.trim(),
      req
    );

    if (!result.success) {
      if (result.details) return res.status(400).json(result);
      return res.status(409).json(result);
    }
    return res.status(201).json(result);
  })
);

app.post('/api/login',
  validate([
    body('username').isString().notEmpty(),
    body('password').isString().notEmpty()
  ]),
  asyncHandler(async (req, res) => {
    const { username, password } = req.body;
    const result = await db.loginUser(username, password);
    if (!result.success) return res.status(401).json(result);

    const tokenPayload = { id: result.user.id, username: result.user.username, role: result.user.role };
    const token = jwt.sign(tokenPayload, JWT_SECRET, { expiresIn: '24h' });

    return res.json({ success: true, message: result.message, user: result.user, token });
  })
);

app.get('/api/me', authenticateToken, asyncHandler(async (req, res) => {
  const user = await db.findUser(req.user.username);
  if (!user) return res.status(404).json({ success: false, message: 'User not found' });
  delete user.password;
  return res.json({ success: true, user });
}));

/* -------------------------
   PROFILE Routes
   ------------------------- */

app.get('/api/profile', authenticateToken, asyncHandler(async (req, res) => {
  const profile = await db.getProfile(req.user.id);
  if (!profile) return res.status(404).json({ success: false, message: 'Profile not found' });
  return res.json({ success: true, data: profile });
}));

app.put('/api/profile',
  authenticateToken,
  validate([
    body('full_name').optional().isString().trim().isLength({ min: 1, max: 255 }),
    body('date_of_birth').optional().isISO8601(),
    body('gender').optional().isString().trim().isLength({ max: 20 }),
    body('phone').optional().isString().trim().isLength({ max: 20 }),
    body('address').optional().isString().trim().isLength({ max: 255 }),
    body('bio').optional().isString()
  ]),
  asyncHandler(async (req, res) => {
    const result = await db.updateProfile(req.user.id, req.body);
    if (!result.success) return res.status(400).json(result);
    return res.json(result);
  })
);

const uploadAvatar = require('./middlewares/uploadAvatar');

app.put(
  '/api/profile/avatar',
  authenticateToken,
  uploadAvatar.single('avatar'),
  async (req, res) => {
    if (!req.file) {
      return res.status(400).json({ success: false, message: 'No file uploaded' });
    }

    const avatar_url =
      `${req.protocol}://${req.get('host')}/uploads/avatars/${req.file.filename}`;

    await db.updateAvatar(req.user.id, avatar_url);

    res.json({
      success: true,
      message: 'Avatar updated',
      avatar_url
    });
  }
);

app.delete(
  '/api/profile/avatar',
  authenticateToken,
  async (req, res) => {
    const avatar_url =
      `${req.protocol}://${req.get('host')}/uploads/avatars/avatar_default.jpg`;

    await db.updateAvatar(req.user.id, avatar_url);

    res.json({
      success: true,
      message: 'Avatar reset to default',
      avatar_url
    });
  }
);


/* -------------------------
   SETTINGS Routes
   ------------------------- */

app.get('/api/settings', authenticateToken, asyncHandler(async (req, res) => {
  const settings = await db.getSettings(req.user.id);
  if (!settings) return res.status(404).json({ success: false, message: 'Settings not found' });
  return res.json({ success: true, data: settings });
}));

app.put('/api/settings',
  authenticateToken,
  validate([
    body('theme').optional().isIn(['light', 'dark']),
    body('language').optional().isString().isLength({ min: 2, max: 10 }),
    body('notification_enabled').optional().isBoolean(),
    body('download_quality').optional().isString().isLength({ max: 20 }),
    body('autoplay_next').optional().isBoolean(),
    body('explicit_filter').optional().isBoolean()
  ]),
  asyncHandler(async (req, res) => {
    const result = await db.updateSettings(req.user.id, req.body);
    if (!result.success) return res.status(400).json(result);
    return res.json(result);
  })
);

// ---------- GENRES ----------
app.get('/api/genres', asyncHandler(async (req, res) => {
  const genres = await db.getAllGenres();
  return res.json({ success: true, data: genres });
}));

app.get('/api/genres/:id',
  validate([ param('id').isInt() ]),
  asyncHandler(async (req, res) => {
    const genre = await db.getGenreById(parseInt(req.params.id));
    if (!genre) {
      return res.status(404).json({ success: false, message: 'Genre not found' });
    }
    return res.json({ success: true, data: genre });
  })
);

/* -------------------------
   Songs Routes
   ------------------------- */
app.get('/api/songs',
  validate([
    query('keyword').optional().isString(),
    query('genreId').optional().toInt(),
    query('artistId').optional().toInt(),
    query('page').optional().toInt(),
    query('limit').optional().toInt()
  ]),
  asyncHandler(async (req, res) => {
    const { keyword, genreId, artistId } = req.query;
    const page = parseInt(req.query.page) || 1;
    const limit = Math.min(parseInt(req.query.limit) || 20, 100);
    const result = await db.getAllSongs(keyword, genreId, artistId, page, limit);
    return res.json(result);
  })
);

app.get('/api/songs/:id',
  validate([ param('id').isInt() ]),
  asyncHandler(async (req, res) => {
    const id = parseInt(req.params.id);
    const song = await db.getSongById(id);
    if (!song) return res.status(404).json({ success: false, message: 'Song not found' });
    return res.json({ success: true, data: song });
  })
);

/* -------------------------
   Artists Routes
   ------------------------- */
app.get('/api/artists', asyncHandler(async (req, res) => {
  const keyword = req.query.keyword || null;
  const artists = await db.getAllArtists(keyword);
  return res.json({ success: true, data: artists });
}));

app.get('/api/artists/:id', asyncHandler(async (req, res) => {
  const id = parseInt(req.params.id);
  const artist = await db.getArtistById(id);
  if (!artist) return res.status(404).json({ success: false, message: 'Not found' });
  return res.json({ success: true, data: artist });
}));

/* -------------------------
   Albums Routes
   ------------------------- */
app.get('/api/albums',
  validate([
    query('keyword').optional().isString(),
    query('artistId').optional().toInt(),
    query('page').optional().toInt(),
    query('limit').optional().toInt()
  ]),
  asyncHandler(async (req, res) => {
    const { keyword, artistId } = req.query;
    const page = parseInt(req.query.page) || 1;
    const limit = Math.min(parseInt(req.query.limit) || 20, 100);
    const result = await db.getAllAlbums(keyword, artistId, page, limit);
    return res.json(result);
  })
);

app.get('/api/albums/:id',
  validate([ param('id').isInt() ]),
  asyncHandler(async (req, res) => {
    const id = parseInt(req.params.id);
    const album = await db.getAlbumById(id);
    if (!album) return res.status(404).json({ success: false, message: 'Album not found' });
    return res.json({ success: true, data: album });
  })
);

/* -------------------------
   Search All Route
   ------------------------- */
app.get('/api/search',
  validate([
    query('keyword').isString().notEmpty(),
    query('page').optional().toInt(),
    query('limit').optional().toInt()
  ]),
  asyncHandler(async (req, res) => {
    const keyword = req.query.keyword;
    const page = parseInt(req.query.page) || 1;
    const limit = Math.min(parseInt(req.query.limit) || 10, 50);
    const result = await db.searchAll(keyword, page, limit);
    return res.json(result);
  })
);


/* -------------------------
   Playlists Routes
   ------------------------- */
app.get('/api/playlists', authenticateToken, asyncHandler(async (req, res) => {
  const playlists = await db.getPlaylistsByUser(req.user.id);
  return res.json({ success: true, data: playlists });
}));

app.post('/api/playlists',
  authenticateToken,
  validate([
    body('name').isString().isLength({ min: 1, max: 255 }),
    body('description').optional().isString(),
    body('isPublic').optional().isBoolean()
  ]),
  asyncHandler(async (req, res) => {
    const { name, description, isPublic } = req.body;
    const playlist = await db.createPlaylist(req.user.id, name, description || null, !!isPublic);
    return res.status(201).json({ success: true, data: playlist });
  })
);

app.get('/api/playlists/:id',
  authenticateToken,
  validate([ param('id').isInt() ]),
  asyncHandler(async (req, res) => {
    const playlist = await db.getPlaylistDetail(
      parseInt(req.params.id),
      req.user.id
    );
    if (!playlist) return res.status(404).json({ success: false, message: 'Playlist not found' });
    return res.json({ success: true, data: playlist });
  })
);

app.delete('/api/playlists/:id',
  authenticateToken,
  validate([ param('id').isInt() ]),
  asyncHandler(async (req, res) => {
    const ok = await db.deletePlaylist(
      parseInt(req.params.id),
      req.user.id
    );
    return res.json({ success: ok, message: ok ? 'Deleted' : 'Not found or already deleted' });
  })
);

app.post('/api/playlists/:id/songs',
  authenticateToken,
  validate([ param('id').isInt(), body('songId').isInt() ]),
  asyncHandler(async (req, res) => {
    const playlistId = parseInt(req.params.id);
    const songId = parseInt(req.body.songId);
    const result = await db.addSongToPlaylist(
      playlistId,
      songId,
      req.user.id
    );
    return res.json(result);
  })
);

app.delete('/api/playlists/:id/songs/:songId',
  authenticateToken,
  validate([ param('id').isInt(), param('songId').isInt() ]),
  asyncHandler(async (req, res) => {
    const ok = await db.removeSongFromPlaylist(
      parseInt(req.params.id),
      parseInt(req.params.songId),
      req.user.id
    );
    return res.json({ success: ok, message: ok ? 'Removed' : 'Not found' });
  })
);

/* -------------------------
   Favorites Routes
   ------------------------- */
app.get('/api/favorites', authenticateToken, asyncHandler(async (req, res) => {
  const favs = await db.getFavorites(req.user.id);
  return res.json({ success: true, data: favs });
}));

app.post('/api/favorites',
  authenticateToken,
  validate([ body('songId').isInt() ]),
  asyncHandler(async (req, res) => {
    const result = await db.addFavorite(req.user.id, parseInt(req.body.songId));
    return res.json(result);
  })
);

app.delete('/api/favorites/:songId',
  authenticateToken,
  validate([ param('songId').isInt() ]),
  asyncHandler(async (req, res) => {
    const ok = await db.removeFavorite(req.user.id, parseInt(req.params.songId));
    return res.json({ success: ok, message: ok ? 'Removed from favorites' : 'Not found' });
  })
);

/* -------------------------
   Histories Routes
   ------------------------- */
app.get('/api/histories',
  authenticateToken,
  validate([ query('limit').optional().toInt() ]),
  asyncHandler(async (req, res) => {
    const limit = Math.min(parseInt(req.query.limit) || 50, 200);
    const histories = await db.getHistories(req.user.id, limit);
    return res.json({ success: true, data: histories });
  })
);

app.post('/api/histories',
  authenticateToken,
  validate([ body('songId').isInt(), body('duration_played').optional().toInt() ]),
  asyncHandler(async (req, res) => {
    const ok = await db.addHistory(req.user.id, parseInt(req.body.songId), parseInt(req.body.duration_played) || 0);
    return res.json({ success: ok });
  })
);

/* -------------------------
   Admin Routes
   ------------------------- */
app.get('/api/admin/users',
  authenticateToken,
  requireRole('admin'),
  asyncHandler(async (req, res) => {
    if (!db.getAllUsers) return res.status(501).json({ success: false, message: 'Not implemented' });
    const users = await db.getAllUsers();
    return res.json({ success: true, data: users });
  })
);

/* -------------------------
   Health and fallback
   ------------------------- */
app.get('/health', (req, res) => res.json({ success: true, uptime: process.uptime() }));

app.use((req, res) => res.status(404).json({ success: false, message: 'Not found' }));

/* -------------------------
   Centralized error handler
   ------------------------- */
app.use((err, req, res, next) => {
  console.error('Unhandled error:', err);
  const status = err.status || 500;
  const payload = {
    success: false,
    message: status === 500 ? 'Internal server error' : err.message
  };
  if (NODE_ENV === 'development') payload.error = err.stack;
  res.status(status).json(payload);
});

/* -------------------------
   Start server
   ------------------------- */
app.listen(PORT, '0.0.0.0', () => {
  console.log(`🚀 Server running on port ${PORT}`);
});