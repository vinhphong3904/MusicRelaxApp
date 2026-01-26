-- 1. T?o Database
CREATE DATABASE MusicAppDB;
GO
USE MusicAppDB;
GO


    CREATE TABLE users (
        id INT IDENTITY(1,1) PRIMARY KEY,
        username NVARCHAR(100) NOT NULL,          -- tên đăng nhập
        email NVARCHAR(255) NOT NULL,             -- email duy nhất
        password_hash NVARCHAR(255) NOT NULL,     -- lưu hash mật khẩu
        role NVARCHAR(20) DEFAULT 'user',         -- user / admin / artist
        status BIT DEFAULT 1,                     -- 1: active, 0: banned/inactive
        avatar_url NVARCHAR(500),                 -- ảnh đại diện
        last_login DATETIME2(3),                  -- lần đăng nhập cuối
        created_at DATETIME2(3) DEFAULT SYSDATETIME(),
        updated_at DATETIME2(3),
        deleted_at DATETIME2(3)                   -- soft delete
    );

    -- Index unique cho email và username (bỏ qua user đã xóa mềm)
    CREATE UNIQUE INDEX IX_Users_Email ON users(email) WHERE deleted_at IS NULL;
    CREATE UNIQUE INDEX IX_Users_Username ON users(username) WHERE deleted_at IS NULL;


    CREATE TABLE profiles (
        id INT IDENTITY(1,1) PRIMARY KEY,
        user_id INT NOT NULL UNIQUE,
        full_name NVARCHAR(255),
        date_of_birth DATE,
        gender NVARCHAR(20),
        phone NVARCHAR(20),
        address NVARCHAR(255),
        bio NVARCHAR(MAX),

        -- Thống kê
        songs_played_count INT DEFAULT 0,       -- Tổng số bài đã nghe
        songs_downloaded_count INT DEFAULT 0,   -- Tổng số bài đã tải
        artists_followed_count INT DEFAULT 0,   -- Tổng số ca sĩ theo dõi

        created_at DATETIME2(3) DEFAULT SYSDATETIME(),
        updated_at DATETIME2(3),

        CONSTRAINT FK_Profile_User FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );


    -- 3. B?ng Artists
    CREATE TABLE artists (
        id INT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(255) NOT NULL,
        bio NVARCHAR(MAX),
        image_url NVARCHAR(500),
        is_verified BIT DEFAULT 0, -- Tick xanh cho ngh? si
        slug NVARCHAR(255), -- Dùng cho URL: son-tung-mtp
        status BIT DEFAULT 1,
        created_at DATETIME2(3) DEFAULT SYSDATETIME(),
        updated_at DATETIME2(3),
        deleted_at DATETIME2(3)
    );

    -- 4. B?ng Artist Follows
    CREATE TABLE artist_follows (
        id INT IDENTITY(1,1) PRIMARY KEY,
        user_id INT NOT NULL,
        artist_id INT NOT NULL,
        followed_at DATETIME2(3) DEFAULT SYSDATETIME(),
        unfollowed_at DATETIME2(3), -- Ðóng vai trò nhu deleted_at

        CONSTRAINT FK_AF_User FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        CONSTRAINT FK_AF_Artist FOREIGN KEY (artist_id) REFERENCES artists(id) ON DELETE CASCADE
    );

    -- Ch? cho phép follow 1 l?n t?i 1 th?i di?m. N?u dã unfollow (có ngày tháng) thì du?c phép insert m?i.
    CREATE UNIQUE INDEX IX_Artist_Follow_Unique ON artist_follows(user_id, artist_id) WHERE unfollowed_at IS NULL;

    -- 5. B?ng Genres
    CREATE TABLE genres (
        id INT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL UNIQUE,
        slug NVARCHAR(100),
        description NVARCHAR(255),
        created_at DATETIME2(3) DEFAULT SYSDATETIME()
    );

    -- 6. B?ng Albums
    CREATE TABLE albums (
        id INT IDENTITY(1,1) PRIMARY KEY,
        title NVARCHAR(255) NOT NULL,
        artist_id INT NOT NULL,
        release_date DATE,
        description NVARCHAR(MAX),
        cover_image_url NVARCHAR(500),
        status BIT DEFAULT 1,
        created_at DATETIME2(3) DEFAULT SYSDATETIME(),
        updated_at DATETIME2(3),
        deleted_at DATETIME2(3),

        CONSTRAINT FK_Albums_Artist FOREIGN KEY (artist_id) REFERENCES artists(id) ON DELETE CASCADE
    );

    -- 7. B?ng Songs
    CREATE TABLE songs (
        id INT IDENTITY(1,1) PRIMARY KEY,
        title NVARCHAR(255) NOT NULL,
        artist_id INT NOT NULL,
        album_id INT, -- Có th? NULL (Single không thu?c album)
        genre_id INT,
        duration_seconds INT NOT NULL, -- Luu giây d? d? tính toán
        audio_url NVARCHAR(500) NOT NULL,
        cover_image_url NVARCHAR(500), -- ?nh bài hát riêng n?u khác album
        lyrics_content NVARCHAR(MAX),
        view_count BIGINT DEFAULT 0, -- Dùng BIGINT d? phòng s? lu?ng view l?n
        slug NVARCHAR(255),
        status BIT DEFAULT 1, -- 1: Public, 0: Draft/Hidden
        created_at DATETIME2(3) DEFAULT SYSDATETIME(),
        updated_at DATETIME2(3),
        deleted_at DATETIME2(3),

        CONSTRAINT FK_Songs_Artist FOREIGN KEY (artist_id) REFERENCES artists(id) ON DELETE NO ACTION, -- Tránh cycle cascade
        CONSTRAINT FK_Songs_Album FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE SET NULL, -- Xóa album, bài hát v?n còn
        CONSTRAINT FK_Songs_Genre FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE SET NULL
    );
    -- Index d? tìm ki?m bài hát nhanh hon
    CREATE INDEX IX_Songs_Title ON songs(title);

    -- 8. B?ng Playlists
    CREATE TABLE playlists (
        id INT IDENTITY(1,1) PRIMARY KEY,
        user_id INT NOT NULL,
        name NVARCHAR(255) NOT NULL,
        is_public BIT DEFAULT 0,
        thumbnail_url NVARCHAR(500),
        description NVARCHAR(255),
        created_at DATETIME2(3) DEFAULT SYSDATETIME(),
        updated_at DATETIME2(3),
        deleted_at DATETIME2(3),

        CONSTRAINT FK_Playlists_User FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

    -- 9. B?ng Playlist Songs (Chi ti?t danh sách phát)
    CREATE TABLE playlist_songs (
        id INT IDENTITY(1,1) PRIMARY KEY,
        playlist_id INT NOT NULL,
        song_id INT NOT NULL,
        order_index INT NOT NULL, -- Th? t? bài hát trong playlist
        added_at DATETIME2(3) DEFAULT SYSDATETIME(),
        
        CONSTRAINT FK_PS_Playlist FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE,
        CONSTRAINT FK_PS_Song FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE
    );
    -- Ch?n thêm trùng bài hát vào 1 playlist
    CREATE UNIQUE INDEX IX_Playlist_Song_Unique ON playlist_songs(playlist_id, song_id);

    -- 10. B?ng Favorites (Yêu thích)
    CREATE TABLE favorites (
        id INT IDENTITY(1,1) PRIMARY KEY,
        user_id INT NOT NULL,
        song_id INT NOT NULL,
        favorited_at DATETIME2(3) DEFAULT SYSDATETIME(),
        deleted_at DATETIME2(3), -- Dùng d? Soft Delete khi b? thích

        CONSTRAINT FK_Fav_User FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        CONSTRAINT FK_Fav_Song FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE
    );
    -- Cho phép like l?i sau khi unlike (Filter index)
    CREATE UNIQUE INDEX IX_Favorites_Unique ON favorites(user_id, song_id) WHERE deleted_at IS NULL;

    -- 11. B?ng Downloaded Songs (Offline mode)
    CREATE TABLE downloaded_songs (
        id INT IDENTITY(1,1) PRIMARY KEY,
        user_id INT NOT NULL,
        song_id INT NOT NULL,
        local_file_path NVARCHAR(500) NOT NULL,
        downloaded_at DATETIME2(3) DEFAULT SYSDATETIME(),

        CONSTRAINT FK_DL_User FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        CONSTRAINT FK_DL_Song FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE
    );

    -- 12. B?ng Reviews (Ðánh giá)
    CREATE TABLE reviews (
        id INT IDENTITY(1,1) PRIMARY KEY,
        user_id INT NOT NULL,
        song_id INT NOT NULL,
        content NVARCHAR(500),
        rating TINYINT CHECK (rating BETWEEN 1 AND 5), -- Dùng TINYINT cho nh?
        created_at DATETIME2(3) DEFAULT SYSDATETIME(),
        updated_at DATETIME2(3),
        deleted_at DATETIME2(3),

        CONSTRAINT FK_Reviews_User FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        CONSTRAINT FK_Reviews_Song FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE
    );
    -- M?i ngu?i ch? review 1 bài 1 l?n (tr? khi dã xóa review cu)
    CREATE UNIQUE INDEX IX_Reviews_Unique ON reviews(user_id, song_id) WHERE deleted_at IS NULL;

    -- 13. B?ng Histories (L?ch s? nghe nh?c)
    CREATE TABLE histories (
        id BIGINT IDENTITY(1,1) PRIMARY KEY, -- Dùng BIGINT vì b?ng này s? r?t l?n
        user_id INT NOT NULL,
        song_id INT NOT NULL,
        played_at DATETIME2(3) DEFAULT SYSDATETIME(),
        duration_played INT, -- Nghe du?c bao nhiêu giây r?i t?t

        CONSTRAINT FK_Hist_User FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        CONSTRAINT FK_Hist_Song FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE
    );
-- Index l?ch s? d? query "Nghe g?n dây" nhanh hon
CREATE INDEX IX_Histories_User_Played ON histories(user_id, played_at DESC);

CREATE TABLE settings (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL UNIQUE, -- Mỗi user chỉ có 1 settings
    theme NVARCHAR(20) DEFAULT 'light',        -- light/dark
    language NVARCHAR(10) DEFAULT 'vi',        -- vi, en...
    notification_enabled BIT DEFAULT 1,        -- Bật/tắt thông báo
    download_quality NVARCHAR(20) DEFAULT '320kbps', -- Chất lượng tải
    autoplay_next BIT DEFAULT 1,               -- Tự động phát tiếp
    explicit_filter BIT DEFAULT 0,             -- Lọc nội dung nhạy cảm

    created_at DATETIME2(3) DEFAULT SYSDATETIME(),
    updated_at DATETIME2(3),

    CONSTRAINT FK_Settings_User FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO users (username, email, password_hash, role, status, avatar_url, created_at)
VALUES
('admin','admin@mail.com',
 '$2a$12$E3ywSQua9gCh10K643w//urAaY//Ioh3S0T16Rtfa6F8KuXp5hfbq',
 'admin',1,'avatar_default.jpg',SYSDATETIME()),

('phong','phong@mail.com',
 '$2a$12$E3ywSQua9gCh10K643w//urAaY//Ioh3S0T16Rtfa6F8KuXp5hfbq',
 'user',1,'avatar_default.jpg',SYSDATETIME()),

('loc','loc@mail.com',
 '$2a$12$E3ywSQua9gCh10K643w//urAaY//Ioh3S0T16Rtfa6F8KuXp5hfbq',
 'user',1,'avatar_default.jpg',SYSDATETIME()),

('kha','kha@mail.com',
 '$2a$12$E3ywSQua9gCh10K643w//urAaY//Ioh3S0T16Rtfa6F8KuXp5hfbq',
 'user',1,'avatar_default.jpg',SYSDATETIME()),

('phu','phu@mail.com',
 '$2a$12$E3ywSQua9gCh10K643w//urAaY//Ioh3S0T16Rtfa6F8KuXp5hfbq',
 'user',1,'avatar_default.jpg',SYSDATETIME()),

('cuong','cuong@mail.com',
 '$2a$12$E3ywSQua9gCh10K643w//urAaY//Ioh3S0T16Rtfa6F8KuXp5hfbq',
 'user',1,'avatar_default.jpg',SYSDATETIME());


INSERT INTO artists (name, bio, image_url, is_verified, slug, status, created_at)
VALUES
(N'Sơn Tùng M-TP',N'Ca sĩ nhạc Pop nổi tiếng','/img/mtp.jpg',1,'son-tung-mtp',1,SYSDATETIME()),
(N'Mỹ Tâm',N'Diva Việt Nam','/img/mytam.jpg',1,'my-tam',1,SYSDATETIME()),
(N'Đen Vâu',N'Rapper Việt Nam','/img/denvau.jpg',1,'den-vau',1,SYSDATETIME()),
(N'Binz',N'Rapper','/img/binz.jpg',1,'binz',1,SYSDATETIME()),
(N'Jack',N'Ca sĩ trẻ','/img/jack.jpg',1,'jack',1,SYSDATETIME()),
(N'Hồ Ngọc Hà',N'Ca sĩ','/img/hoha.jpg',1,'ho-ngoc-ha',1,SYSDATETIME()),
(N'Noo Phước Thịnh',N'Ca sĩ','/img/noo.jpg',1,'noo-phuoc-thinh',1,SYSDATETIME()),
(N'Erik',N'Ca sĩ trẻ','/img/erik.jpg',1,'erik',1,SYSDATETIME()),
(N'Min',N'Ca sĩ','/img/min.jpg',1,'min',1,SYSDATETIME()),
(N'JustaTee',N'Ca sĩ/Rapper','/img/justatee.jpg',1,'justatee',1,SYSDATETIME()),
(N'Karik',N'Rapper','/img/karik.jpg',1,'karik',1,SYSDATETIME()),
(N'Trịnh Công Sơn',N'Nhạc sĩ huyền thoại','/img/tcs.jpg',1,'trinh-cong-son',1,SYSDATETIME()),
(N'Đàm Vĩnh Hưng',N'Ca sĩ','/img/dvh.jpg',1,'dam-vinh-hung',1,SYSDATETIME()),
(N'Bích Phương',N'Ca sĩ','/img/bichphuong.jpg',1,'bich-phuong',1,SYSDATETIME()),
(N'Vũ',N'Indie singer','/img/vu.jpg',1,'vu',1,SYSDATETIME()),
(N'AMEE',N'Ca sĩ trẻ','/img/amee.jpg',1,'amee',1,SYSDATETIME()),
(N'Soobin Hoàng Sơn',N'Ca sĩ','/img/soobin.jpg',1,'soobin-hoang-son',1,SYSDATETIME()),
(N'Hoàng Dũng',N'Ca sĩ','/img/hoangdung.jpg',1,'hoang-dung',1,SYSDATETIME()),
(N'Orange',N'Ca sĩ','/img/orange.jpg',1,'orange',1,SYSDATETIME()),
(N'Kay Trần',N'Ca sĩ','/img/kaytran.jpg',1,'kay-tran',1,SYSDATETIME());
INSERT INTO albums (title, artist_id, release_date, description, cover_image_url)
VALUES
(N'Sky Tour',1,'2019-07-01', N'Album tuyển chọn Sơn Tùng','/covers/mtp_sky.jpg'),
(N'Tâm 9',2,'2017-12-01', N'Album của Mỹ Tâm','/covers/mytam_tam9.jpg'),
(N'Đen EP',3,'2018-05-01', N'Tuyển rap của Đen','/covers/den_ep.jpg'),
(N'Love Songs',4,'2019-09-01', N'Album Binz','/covers/binz_love.jpg'),
(N'Jack Hits',5,'2020-03-01', N'Tuyển chọn Jack','/covers/jack_hits.jpg'),
(N'Cả Một Trời Thương Nhớ',6,'2016-10-01', N'Hồ Ngọc Hà tuyển','/covers/hoha.jpg'),
(N'Noo Collection',7,'2018-11-01', N'Noo Phước Thịnh','/covers/noo.jpg'),
(N'Erik Singles',8,'2019-06-01', N'Erik tuyển','/covers/erik.jpg'),
(N'Min Best',9,'2020-02-01', N'Min tuyển','/covers/min.jpg'),
(N'JustaTee Mix',10,'2017-08-01', N'JustaTee tuyển','/covers/justatee.jpg'),
(N'Karik Hits',11,'2018-04-01', N'Karik tuyển','/covers/karik.jpg'),
(N'Trịnh Công Sơn Tuyển',12,'2000-01-01', N'Tuyển Trịnh Công Sơn','/covers/tcs.jpg'),
(N'Mr. Dam',13,'2015-05-01', N'Đàm Vĩnh Hưng tuyển','/covers/dvh.jpg'),
(N'Bích Phương Hits',14,'2019-11-01', N'Bích Phương tuyển','/covers/bp.jpg'),
(N'Vũ Indie',15,'2016-06-01', N'Vũ tuyển','/covers/vu.jpg'),
(N'AMEE Debut',16,'2020-01-01', N'AMEE tuyển','/covers/amee.jpg'),
(N'Soobin Collection',17,'2018-09-01', N'Soobin tuyển','/covers/soobin.jpg'),
(N'Hoàng Dũng Album',18,'2019-03-01', N'Hoàng Dũng tuyển','/covers/hd.jpg'),
(N'Orange Singles',19,'2020-07-01', N'Orange tuyển','/covers/orange.jpg'),
(N'KayTrần Hits',20,'2019-12-01', N'Kay Trần tuyển','/covers/kaytran.jpg');
INSERT INTO genres (name, slug, description)
VALUES
('Pop','pop',N'Nhạc đại chúng'),
('Ballad','ballad',N'Nhạc buồn, chậm'),
('Rap','rap',N'Hip hop / Rap'),
('R&B','rnb',N'Rhythm and Blues'),
('EDM','edm',N'Electronic Dance Music'),
('Indie','indie',N'Nhạc độc lập'),
('Rock','rock',N'Nhạc Rock'),
('Folk','folk',N'Nhạc dân gian hiện đại'),
('Acoustic','acoustic',N'Phiên bản mộc'),
('Classical','classical',N'Nhạc cổ điển');

INSERT INTO songs 
(title, artist_id, album_id, genre_id, duration_seconds, audio_url, cover_image_url, lyrics_content, view_count, slug, status, created_at)
VALUES
(N'Chúng Ta Của Hiện Tại',1,1,1,250,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','mtp_ctcht.jpg',NULL, 523456,'chung-ta-cua-hien-tai',1,SYSDATETIME()),
(N'Lạc Trôi',1,1,1,240,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','mtp_lactroi.jpg',NULL, 812345,'lac-troi',1,SYSDATETIME()),

(N'Ước Gì',2,2,2,260,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','mytam_uocgi.jpg',NULL, 234567,'uoc-gi',1,SYSDATETIME()),
(N'Họa Mi Tóc Nâu',2,2,2,270,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','mytam_hoami.jpg',NULL, 345678,'hoa-mi-toc-nau',1,SYSDATETIME()),

(N'Hai Triệu Năm',3,3,3,230,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','den_haitrieu.jpg',NULL, 456789,'hai-trieu-nam',1,SYSDATETIME()),
(N'Bài Này Chill Phết',3,3,6,220,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','den_chill.jpg',NULL, 567890,'bai-nay-chill-phet',1,SYSDATETIME()),

(N'Bigcityboi',4,4,3,210,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3','binz_bigcityboi.jpg',NULL, 678901,'bigcityboi',1,SYSDATETIME()),
(N'OK',4,4,3,200,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3','binz_ok.jpg',NULL, 789012,'ok',1,SYSDATETIME()),

(N'Hoa Hải Đường',5,5,2,240,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3','jack_hoahaiduong.jpg',NULL, 890123,'hoa-hai-duong',1,SYSDATETIME()),
(N'Đom Đóm',5,5,1,250,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3','jack_domdom.jpg',NULL, 901234,'dom-dom',1,SYSDATETIME()),

(N'Cả Một Trời Thương Nhớ',6,6,2,260,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3','hoha_camot.jpg',NULL, 345123,'ca-mot-troi-thuong-nho',1,SYSDATETIME()),
(N'Em Muốn Anh Đưa Em Về',6,6,1,240,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3','hoha_emmuon.jpg',NULL, 456234,'em-muon-anh-dua-em-ve',1,SYSDATETIME()),

(N'Cause I Love You',7,7,1,230,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3','noo_causeiloveu.jpg',NULL, 567345,'cause-i-love-you',1,SYSDATETIME()),
(N'Thương Em Là Điều Anh Không Thể Ngờ',7,7,1,250,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3','noo_thuongem.jpg',NULL, 678456,'thuong-em-la-dieu-anh-khong-the-ngo',1,SYSDATETIME()),

(N'Sau Tất Cả',8,8,2,240,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3','erik_sautatca.jpg',NULL, 789567,'sau-tat-ca',1,SYSDATETIME()),
(N'Em Không Sai Chúng Ta Sai',8,8,2,250,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3','erik_emkhongsai.jpg',NULL, 890678,'em-khong-sai-chung-ta-sai',1,SYSDATETIME()),

(N'Có Em Chờ',9,9,1,230,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3','min_coemcho.jpg',NULL, 901789,'co-em-cho',1,SYSDATETIME()),
(N'Gọi Tên Em',9,9,1,220,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3','min_goitenem.jpg',NULL, 123890,'goi-ten-em',1,SYSDATETIME()),

(N'Forever Alone',10,10,3,210,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3','justatee_forever.jpg',NULL, 234901,'forever-alone',1,SYSDATETIME()),
(N'Thằng Điên',10,10,3,240,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3','justatee_thangdien.jpg',NULL, 345012,'thang-dien',1,SYSDATETIME()),

(N'Người Lạ Ơi',11,11,3,230,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3','karik_nguoilao.jpg',NULL, 456123,'nguoi-la-oi',1,SYSDATETIME()),
(N'Anh Không Đòi Quà',11,11,3,220,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3','karik_anhkhongdoi.jpg',NULL, 567234,'anh-khong-doi-qua',1,SYSDATETIME()),

(N'Diễm Xưa',12,12,9,300,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3','tcs_diemxua.jpg',NULL, 678345,'diem-xua',1,SYSDATETIME()),
(N'Cát Bụi',12,12,9,280,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3','tcs_catbui.jpg',NULL, 789456,'cat-bui',1,SYSDATETIME()),

(N'Nửa Vầng Trăng',13,13,2,260,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','dvh_nuavangtrang.jpg',NULL, 890567,'nua-vang-trang',1,SYSDATETIME()),
(N'Say Tình',13,13,2,250,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','dvh_saytinh.jpg',NULL, 901678,'say-tinh',1,SYSDATETIME()),

(N'Bao Giờ Lấy Chồng',14,14,1,240,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','bichphuong_baogio.jpg',NULL, 123789,'bao-gio-lay-chong',1,SYSDATETIME()),
(N'Bùa Yêu',14,14,1,250,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','bichphuong_buayeu.jpg',NULL, 234890,'bua-yeu',1,SYSDATETIME()),

(N'Lạ Lùng',15,15,6,230,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','vu_lalung.jpg',NULL, 345901,'la-lung',1,SYSDATETIME()),
(N'Mùa Mưa Ngâu',15,15,6,240,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','vu_muamua.jpg',NULL, 456012,'mua-mua-ngau',1,SYSDATETIME()),

(N'Anh Nhà Ở Đâu Thế',16,16,1,220,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','amee_anhnhao.jpg',NULL, 567123,'anh-nha-o-dau-the',1,SYSDATETIME()),
(N'Sao Anh Chưa Về Nhà',16,16,1,230,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','amee_saoanh.jpg',NULL, 678234,'sao-anh-chua-ve-nha',1,SYSDATETIME()),

(N'Phía Sau Một Cô Gái',17,17,1,240,'soobin_phiasau.mp3','soobin_phiasau.jpg',NULL, 789345,'phia-sau-mot-co-gai',1,SYSDATETIME()),
(N'Đi Để Trở Về',17,17,1,250,'soobin_didetrove.mp3','soobin_didetrove.jpg',NULL, 890456,'di-de-tro-ve',1,SYSDATETIME()),

(N'Nàng Thơ',18,18,2,260,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','hoangdung_nangtho.jpg',NULL, 901567,'nang-tho',1,SYSDATETIME()),
(N'Yên Bình',18,18,2,270,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','hoangdung_yenbinh.jpg',NULL, 123678,'yen-binh',1,SYSDATETIME()),

(N'Tình Nhân Ơi',19,19,1,240,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','orange_tinhnhan.jpg',NULL, 234789,'tinh-nhan-oi',1,SYSDATETIME()),
(N'Khi Em Lớn',19,19,1,250,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','orange_khiemlon.jpg',NULL,135678,'khi-em-lon',1,SYSDATETIME()),

(N'Chuyện Tình Tôi',20,20,1,230,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','kaytran_chuyentinh.jpg',NULL, 43434, 'chuyen-tinh-toi',1,SYSDATETIME()),
(N'Nắm Đôi Bàn Tay',20,20,1,240,'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3','kaytran_namdoi.jpg',NULL, 23245, 'nam-doi-ban-tay',1,SYSDATETIME());

-- Sơn Tùng M-TP
UPDATE songs
SET lyrics_content = N'Mùa thu mang giấc mơ quay về
Vẫn nguyên vẹn như hôm nào
Lá bay theo gió xôn xao
Chốn xưa em chờ (chốn xưa em chờ)
Đoạn đường ngày nào nơi ta từng đón đưa
Còn vấn vương không phai mờ
Giấu yêu thương trong vần thơ
Chúng ta...
Là áng mây bên trời vội vàng ngang qua
Chúng ta...
Chẳng thể nâng niu những câu thề
Cứ như vậy thôi, không một lời, lặng lẽ thế chia xa
Chiều mưa bên hiên vắng buồn
Còn ai thương ai, mong ai?
Điều anh luôn giữ kín trong tim
Thương em đôi mắt ướt nhòa
Điều anh luôn giữ kín trong tim này
Thương em đâu đó khóc òa
Điều anh luôn giữ kín trong tim này
Ngày mai nắng gió, sương hao gầy
Có ai thương, lắng lo cho em? (Whoo-whoo-whoo)
Điều anh luôn giữ kín trong tim
Thương em, anh mãi xin là
Điều anh luôn giữ kín trong tim này
Thương em vì thương thôi mà
Điều anh luôn giữ kín trong tim này
Dù cho nắng tắt, xuân thay màu
Héo khô đi tháng năm xưa kia
(Anh nguyện ghi mãi trong tim)
"Nắng vương trên cành héo khô những kỉ niệm xưa kia"
"Ngày mai, người luyến lưu về giấc mơ từng có, liệu có ta?"
Có anh nơi đó không?
Có anh nơi đó không?
(Liệu có ta?)
Chúng ta...
Là áng mây bên trời vội vàng ngang qua
Chúng ta...
Chẳng thể nâng niu những câu thề
Cứ như vậy thôi, không một lời, lặng lẽ thế chia xa
Chiều mưa bên hiên vắng buồn
Còn ai thương ai, mong ai?
Điều anh luôn giữ kín trong tim
Thương em đôi mắt ướt nhòa
Điều anh luôn giữ kín trong tim này
Thương em đâu đó khóc òa
Điều anh luôn giữ kín trong tim này
Ngày mai nắng gió, sương hao gầy
Có ai thương, lắng lo cho em? (Whoo-whoo-whoo)
Điều anh luôn giữ kín trong tim
Thương em, anh mãi xin là
Điều anh luôn giữ kín trong tim này
Thương em vì thương thôi mà
Điều anh luôn giữ kín trong tim này
Dù cho nắng tắt, xuân thay màu
Héo khô đi tháng năm xưa kia
(Anh nguyện ghi mãi trong tim)
No, no, no
No, no, no
Điều anh luôn giữ kín trong tim (giữ kín trong tim này)
Giữ mãi trong tim này (giữ mãi trong tim này)
Giữ mãi trong tim mình (giữ mãi trong tim mình)
Giữ...
Có anh nơi đó không?
Có anh nơi đó không?
(Whoo-whoo-whoo-whoo)
Điều anh luôn giữ kín trong tim (no, no)
Điều anh luôn giữ kín trong tim này (no, no)
Điều anh luôn giữ kín trong tim này
(Ngày mai, nắng gió, sương hao gầy)
(Có ai thương, lắng lo cho em?)
Điều anh luôn giữ kín trong tim (no, no)
Điều anh luôn giữ kín trong tim này (no, no)
Điều anh luôn giữ kín trong tim này
(Dù cho nắng tắt, xuân thay màu)
(Héo khô đi tháng năm xưa kia)
(Anh nguyện ghi mãi trong tim)
Điều anh luôn giữ kín trong tim
Thương em đôi mắt ướt nhòa
Điều anh luôn giữ kín trong tim này
Thương em đâu đó khóc òa
Điều anh luôn giữ kín trong tim này
Ngày mai nắng gió, sương hao gầy
Có ai thương, lắng lo cho em? (Whoo-whoo-whoo)
Điều anh luôn giữ kín trong tim
Thương em, anh mãi xin là
Điều anh luôn giữ kín trong tim này
Thương em vì thương thôi mà
Điều anh luôn giữ kín trong tim này
Dù cho nắng tắt, xuân thay màu
Héo khô đi tháng năm xưa kia
(Anh nguyện ghi mãi trong tim)'
WHERE title = N'Chúng Ta Của Hiện Tại';

UPDATE songs 
SET lyrics_content = N'Ah ah
Người theo hương hoa mây mù giăng lối
Làn sương khói phôi phai đưa bước ai xa rồi
Đơn côi mình ta vấn vương
Hồi ức trong men say chiều mưa buồn
Ngăn giọt lệ ngừng khiến khoé mi sầu bi
Đường xưa nơi cố nhân từ giã biệt li (cánh hoa rụng rời)
Phận duyên mong manh rẽ lối trong mơ ngày tương phùng
Oh tiếng khóc cuốn theo làn gió bay
Thuyền qua sông lỡ quên vớt ánh trăng tàn nơi này
Trống vắng bóng ai dần hao gầy hoh
Lòng ta xin nguyện khắc ghi trong tim tình nồng mê say
Mặc cho tóc mây vương lên đôi môi cay
Bâng khuâng mình ta lạc trôi giữa đời
Ta lạc trôi giữa trời
Đôi chân lang thang về nơi đâu
Bao yêu thương giờ nơi đâu
Câu thơ tình xưa vội phai mờ
Theo làn sương tan biến trong cõi mơ
Mưa bụi vươn trên làn mi mắt (mắt)
Ngày chia lìa hoa rơi buồn hiu hắt (hắt)
Tiếng đàn ai thêm sầu tương tư lặng mình trong chiều hoàng hôn tan vào lời ca
Lối mòn đường vắng một mình ta
Nắng chiều vàng úa nhuộm ngày qua
Xin đừng quay lưng xoá
Đừng mang câu hẹn ước kia rời xa
Yên bình nơi nào đây
Chôn vùi theo làn mây yeah lala
Người theo hương hoa mây mù giăng lối
Làn sương khói phôi phai đưa bước ai xa rồi
Đơn côi mình ta vấn vương
Hồi ức trong men say chiều mưa buồn
Ngăn giọt lệ ngừng khiến khoé mi sầu bi
Từ xưa nơi cố nhân từ giã biệt li (cánh hoa rụng rời)
Phận duyên mong manh rẽ lối trong mưa ngày tương phùng oh
Tiếng khóc cuốn theo làn gió bay
Thuyền qua sông lỡ quên vớt ánh trăng tàn nơi này
Trống vắng bóng ai dần hao gầy hoh
Lòng ta xin nguyện khắc ghi trong tim tình nồng mê say
Mặc cho tóc mây vương lên đôi môi cay
Bâng khuâng mình ta lạc trôi giữa đời
Ta lạc trôi giữa trời ah
Ta lạc trôi lạc trôi (lạc trôi)
Ta lạc trôi giữa đời
Lạc trôi giữa trời
Yeah ah ah
Ta đang lạc nơi nào (lạc nơi nào)
Ta đang lạc nơi nào
Lối mòn đường vắng một mình ta
Ta đang lạc nơi nào (ai bên cạnh ta ai bên cạnh ta)
Nắng chiều vàng úa nhuộm ngày qua
Ta đang lạc nơi nào oh'
WHERE title = N'Lạc Trôi';

-- Mỹ Tâm
UPDATE songs 
SET lyrics_content = N'Em đã sống những đêm trời có ánh trăng chiếu vàng
Em đã sống những đêm ngoài kia biển ru bờ cát
Ước gì anh ở đây giờ này
Ước gì anh cùng em chuyện trò
Cùng nhau nghe sóng xô ghềnh đá ngàn câu hát yên bình
Em đã biết cô đơn là thế mỗi khi cách xa anh
Từng đàn chim cuối chân trời biết tìm nơi bình yên
Ước gì anh ở đây giờ này
Ước gì em được nghe giọng cười
Và hơi ấm đã bao ngày qua mình luôn sát vai kề
Em xa anh đã bao ngày rồi, nghe như tháng năm ngừng trôi
Đi xa em nhớ anh thật nhiều, này người người yêu anh hỡi
Ước gì em đã không lỡ lời
Ước gì ta đừng có giận hờn
Để giờ đây cô đơn vắng tanh
Đời em đã vắng anh rồi
Ước gì cho thời gian trở lại
Ước gì em gặp anh một lần
Em sẽ nói em luôn nhớ anh
Và em chỉ có anh thôi
Em đã sống những đêm trời có ánh trăng chiếu vàng
Em đã sống những đêm ngoài kia biển ru bờ cát
Ước gì anh ở đây giờ này
Ước gì anh cùng em chuyện trò
Cùng nhau nghe sóng xô ghềnh đá ngàn câu hát yên bình
Em đã biết cô đơn là thế mỗi khi cách xa anh
Từng đàn chim cuối chân trời biết tìm nơi bình yên
Ước gì anh ở đây giờ này
Ước gì em được nghe giọng cười
Và hơi ấm đã bao ngày qua mình luôn sát vai kề
Em xa anh đã bao ngày rồi, nghe như tháng năm ngừng trôi
Đi xa em nhớ anh thật nhiều, này người người yêu anh hỡi
Ước gì em đã không lỡ lời
Ước gì ta đừng có giận hờn
Để giờ đây cô đơn vắng tanh
Đời em đã vắng anh rồi
Ước gì cho thời gian trở lại
Ước gì em gặp anh một lần
Em sẽ nói em luôn nhớ anh
Và em chỉ có anh thôi
Ước gì cho thời gian trở lại
Ước gì em gặp anh một lần
Em sẽ nói em luôn nhớ anh
Và em chỉ có anh thôi
Ước gì em đã không lỡ lời
Ước gì ta đừng có giận hờn
Để giờ đây cô đơn vắng tanh
Đời em đã vắng anh rồi
Ước gì cho thời gian trở lại
Ước gì em gặp anh một lần
Em sẽ nói em luôn nhớ anh
Và em chỉ có anh thôi' 
WHERE title = N'Ước Gì';

UPDATE songs 
SET lyrics_content = N'Họa mi hót giữa bầu trời xanh
Họa mi long lanh chào ngày mới
Họa mi tới những khu vườn thơ
Họa mi chơ vơ lúc đêm về
Từng tia nắng thắp lên mùa xuân
Từng mùa xuân êm đềm em hát
Và em hát lúc tuổi còn thơ
Từ tuổi thơ em biết mơ mộng
Em, họa mi yêu dấu hay mộng mơ
Hay e ấp như ngày còn thơ
Em, tình yêu đến nhưng còn ngủ mơ
Nên em vẫn là người bơ vơ
Họa mi, họa mi hót trong vườn khuya
Họa mi, họa mi tóc nâu là em đó
Họa mi, họa mi hót trên bầu trời xanh
Họa mi, họa mi mong manh những tiếng cười
Họa mi, họa mi hót trong vườn khuya
Họa mi, hoạ mi tóc nâu là em đó
Họa mi, họa mi hót trên bầu trời xanh
Họa mi, họa mi mong manh những tiếng cười
Na-na-na-na-na-na-na-na-na-na
Na-na-na, na-na-na, na-na-na
Họa mi hót giữa bầu trời xanh
Họa mi long lanh chào ngày mới
Họa mi tới những khu vườn thơ
Họa mi chơ vơ lúc đêm về
Từng tia nắng thắp lên mùa xuân (aha)
Từng mùa xuân êm đềm em hát (aha)
Và em hát lúc tuổi còn thơ
Từ tuổi thơ em biết mơ mộng
Em, họa mi yêu dấu hay mộng mơ
Hay e ấp như ngày còn thơ
Em, tình yêu đến nhưng còn ngủ mơ
Nên em vẫn là người bơ vơ
Họa mi, họa mi hót trong vườn khuya
Họa mi, họa mi tóc nâu là em đó
Họa mi, họa mi hót trên bầu trời xanh
Họa mi, họa mi mong manh những tiếng cười
Họa mi, họa mi hót trong vườn khuya
Họa mi, hoạ mi tóc nâu là em đó
Họa mi, họa mi hót trên bầu trời xanh
Họa mi, họa mi mong manh những tiếng cười
Hoạ mi, hoạ mi hót (trong vườn khuya)
Họa mi, họa mi tóc nâu là em đó
Họa mi, họa mi hót trên bầu trời xanh
Họa mi, họa mi mong manh những tiếng cười
Họa mi, họa mi hót (trong vườn khuya)
Họa mi, hoạ mi tóc nâu là em đó
Họa mi, họa mi hót trên bầu trời xanh
Họa mi, họa mi mong manh những tiếng cười
Họa mi mong manh những tiếng cười' 
WHERE title = N'Họa Mi Tóc Nâu';

-- Đen Vâu
UPDATE songs 
SET lyrics_content = N'Anh cô đơn giữa tinh không này
Muôn con sóng cuốn xô vào đây
Em cô đơn giữa mênh mông người
Và ta cô đơn đã hai triệu năm
Anh cô đơn giữa tinh không này
Muôn con sóng cuốn xô vào đây
Em cô đơn giữa mênh mông người
Và ta cô đơn đã hai triệu năm (yah)
Xung quanh anh toàn là nước ay
Cơ thể anh đang bị ướt ay
Mênh mông toàn là nước ay
Êm ái như chưa từng trước đây
Trăm ngàn con sóng xô (sóng xô ya)
Anh lao vào trong biển cả vì em làm anh nóng khô (nóng khô ya)
Anh ngâm mình trong làn nước để mặn mòi từ da dẻ (mặn mòi từ da dẻ)
Ta cần tình yêu vì tình yêu làm cho ta trẻ đúng rồi (ta trẻ ta trẻ ta trẻ)
Anh cũng cần em nhưng không biết em sao
Anh không care lắm và anh quyết đem trao
Cho em hết nắng cho em hết đêm sao
Nhìn mặt anh đi em nghĩ anh tiếc em sao yo (anh thấy tiếc em đâu yo)
Trăm ngàn con sóng từ mọi nơi mà đổ về
Và đây là cách mà anh đi tìm kiếm sự vỗ về
Em có quá nhiều bí mật anh thì không cần gặng hỏi
Em sâu như là đại dương anh thì không hề lặn giỏi yo yo (anh thì không hề lặn giỏi baby)
Anh soi mình vào gương cho bõ công lau
Thấy mặt thấy người sao thấy rõ trong nhau
Ánh mắt nụ cười kia không rõ nông sâu
Ta rồi sẽ là ai một câu hỏi nhỏ trong đầu (một câu hỏi nhỏ trong đầu)
Ta chỉ là hòn đất hay chỉ là cỏ bông lau (ta chỉ là cỏ bông lau)
Như là mấy gã em mới bỏ không lâu (như là mấy gã em mới bỏ không lâu)
Hay chỉ là đầu thuốc kia cháy đỏ không lâu yo (cháy đỏ không lâu)
Yêu em kiểu nông dân yêu em kiểu quê mùa
Yêu từ vụ đông xuân đến hè thu thay mùa
Nhưng em thì trơn trượt như là con cá chuối (như là con cá chuối)
Muốn níu em trong tay Khá Bảnh cũng khá đuối (Khá Bảnh cũng khá đuối)
Em giống hệt như biển cả em có nhiều bí mật
Anh làm rất nhiều thứ để đồng tiền trong ví chật
Người ta không quý con ong mà người ta chỉ quý mật
Em hỏi anh nhạc sao hay anh gọi nó là bí thuật yo yo
Em hỏi anh nhạc sao hay anh gọi nó là bí thuật yo
Anh cô đơn giữa tinh không này
Muôn con sóng cuốn xô vào đây
Em cô đơn giữa mênh mông người
Và ta cô đơn đã hai triệu năm
Anh cô đơn giữa tinh không này
Muôn con sóng cuốn xô vào đây
Em cô đơn giữa mênh mông người
Và ta cô đơn đã hai triệu năm
Nước đã hình thành trong hàng triệu năm (triệu năm)
Cát đã hình thành trong hàng triệu năm (triệu năm)
Biển cả hình thành trong hàng triệu năm (triệu năm)
Và em làm anh buồn sau hàng triệu năm (triệu năm)
Gặp em từ thể đơn bào rồi tiến hoá (tiến hoá)
Xa em từ khi thềm lục địa đầy biến hoá (tha hoá)
Muốn được ôm em qua kỷ Jura
Hoá thạch cùng nhau trên những phiến đá (phá đá cùng nhau)
Rồi loài người tìm thấy lửa anh lại tìm thấy em (yah)
Anh tưởng rằng mọi thứ sẽ được bùng cháy lên (yah)
Muốn được cùng em trồng rau bên hồ cá (hồ cá)
Nhưng tim em lúc đó đang là thời kì đồ đá (đang là thời kì đồ đá)
Hey anh đã tin vào em như tin vào thuyết nhật tâm
Như Ga-li-lê người ta nói anh thật hâm
Có lẽ Đác-win biết biển cả sẽ khô hơn
Nhưng anh tin ông ta không biết chúng ta đang tiến hoá để cô đơn (tiến hoá để cô đơn)
Và có lẽ Đác-win biết biển cả sẽ khô hơn (tiến hoá để cô đơn)
Nhưng anh tin ông ta không biết chúng ta đang tiến hoá để cô đơn (tiến hoá để cô đơn tiến hoá để cô đơn)
Anh cô đơn giữa tinh không này
Muôn con sóng cuốn xô vào đây
Em cô đơn giữa mênh mông người
Và ta cô đơn đã hai triệu năm
Anh cô đơn giữa tinh không này
Muôn con sóng cuốn xô vào đây
Em cô đơn giữa mênh mông người
Và ta cô đơn đã hai triệu năm
Anh cô đơn giữa tinh không này
Muôn con sóng cuốn xô vào đây
Em cô đơn giữa mênh mông người
Và ta cô đơn đã hai triệu năm
Anh cô đơn giữa tinh không này
Muôn con sóng cuốn xô vào đây
Em cô đơn giữa mênh mông người
Và ta cô đơn đã hai triệu năm' 
WHERE title = N'Hai Triệu Năm';

UPDATE songs 
SET lyrics_content = N'I just wanna chill with you tonight
And all the sorrow left behind uh-way
Sometimes I feel lost in the crowd
Life is full of ups and downs
But i''t''s alright, I feel peaceful inside
Ay, ya
Em dạo này ổn không? Còn đi làm ở công ty cũ?
Còn đi sớm về hôm nhưng mà đồng lương vẫn không khi đủ? (ay)
Đồng nghiệp của em thế nào, trong thang máy có chào với nhau?
Có nói qua nói lại và những cuộc họp có đào bới nhau?
Sếp của em thế nào? Dễ tính hay thường gắt gỏng?
Anh ta có thương nhân viên hay thường buông lời sắc mỏng? (ah)
Em còn thiếu ngủ trong những lần phải chạy deadline
Em quên ăn quên uống, quên cả việc chải lại tóc tai (ay)
Những đôi giày cao gót chắc còn làm đau em
Và tiền bao nhiêu cho đủ, ai biết ngày sau em
Mắt em còn mỏi không? Tám tiếng nhìn màn hình
Những tối đi về đơn độc em thấy lòng mình lặng thinh
Và đừng để đời chỉ là những chuỗi ngày được chấm công (tha''t''s right)
Miệng cười như nắng hạ nhưng trong lòng thì chớm đông (yo)
Nếu mà mệt quá giữa thành phố sống chồng lên nhau
Cùng lắm thì mình về quê, mình nuôi cá và trồng thêm rau (ha-ha)
Trời thả vạt nắng khiến đám tóc em hoe vàng
Chiều nay đi giữa thành phố em bỗng thấy sao mơ màng
Tìm cho mình một không gian, bật bài nhạc làm em chill
Tâm hồn em phiêu dạt theo áng mây bên trời
Trời thả vạt nắng khiến đám tóc em hoe vàng
Chiều nay đi giữa thành phố em bỗng thấy sao mơ màng
Tìm cho mình một không gian, bật bài nhạc làm em chill
Tâm hồn em phiêu dạt theo áng mây bên trời
Anh dạo này cũng bận nhiều và cũng có thêm nhiều đêm diễn
Âm nhạc mở lối cuộc đời anh như là ngọn hải đăng ở trên biển
Anh được gặp những người nổi tiếng trước giờ chỉ thấy trên tivi
Gặp ''H''Hen Niê hoa hậu, gặp cả Sơn Tùng M-TP, ya
Đi hát vui lắm em vì đồng âm của anh họ rất tuyệt (yeah)
Bọn anh hát cùng nhau khiến cho thanh xuân này như bất diệt
Anh thấy mình không cô đơn, không áp lực nào buộc chân anh
Nhiều khi anh lên sân khấu mà dưới khán giả họ thuộc hơn anh
Anh cũng có những hợp đồng, những điều khoản mà anh phải dần quen
Anh cũng cần tiền, những dự án họ nói họ cần Đen (yeah)
Và những con số nặng tới mức đủ sức làm choáng mình
Nhưng em yên tâm anh bán chất xám chứ chưa từng bán mình (ha-ha)
Nhưng cũng có lúc mọi thứ không như là những gì ta muốn
Thế giới này vận hành theo cái cách luôn ghì ta xuống, oh
Nhưng mà mộng mơ anh nhiều như niêu cơm của Thạch Sanh (yeah)
Ai muốn lấy cứ lấy-ya, không thể nào mà sạch banh
Trời thả vạt nắng khiến đám tóc em hoe vàng
Chiều nay đi giữa thành phố em bỗng thấy sao mơ màng
Tìm cho mình một không gian, bật bài nhạc làm em chill
Tâm hồn em phiêu dạt theo áng mây bên trời
Trời thả vạt nắng khiến đám tóc em hoe vàng
Chiều nay đi giữa thành phố em bỗng thấy sao mơ màng
Tìm cho mình một không gian, bật bài nhạc làm em chill
Tâm hồn em phiêu dạt theo áng mây bên trời
Mình sướng hơn những người giàu nhỉ (ay)
Vầng trán mình chưa hề nhàu nhĩ (ay)
Dù chênh vênh như là cầu khỉ (ay)
Đời sóng gió mình là tàu thuỷ (ay)
Vì một ngày còn sống
Là một ngày đắm say (một ngày đắm say)
Ngày đẹp trời nhất
Là ngày mình còn nắm tay (ngày còn nắm tay)
Mình sẽ không ngã
Vì mình ngã họ hả hê (ay)
Biển người cũng là biển
Cho tụi mình tắm thoả thuê
Và chúng ta sẽ không
Là một ai trông giống họ (một ai trông giống họ)
Sẽ không rỗng tuếch
Như một cái chai trong đống lọ (chai trong đống lọ)
Sáng chúng ta làm vì tờ bạc nhiều màu trong ví
Đêm về ta chill, riêng mình một bầu không khí
Vì tim ta còn trẻ dù thân xác ta sẽ già
Nhưng mà ta không ủ rũ như là mấy con sẻ già (yeah)
Chúng ta có những chiều vàng, dắt tay nhau lên đồi xa
Nắng khoác lên mình lớp áo, nheo mắt lại nhìn trời hoa
Và những đêm đen huyền dịu cho tiếng lòng thêm dõng dạc
Ta thấy nhau bằng tâm hồn và không cần nhìn bằng võng mạc (yes)
Ta sẽ cố để có được những thứ mà ta chờ mong
Dưới ngọn đồi, căn nhà nhỏ, nhìn ra bờ sông (nhìn ra bờ sông)
Vì anh chưa từng mơ ngày nào đó mình trở thành siêu sao (siêu sao)
Từ ngày thơ bé anh đã muốn trở thành chưởng môn phái Tiêu Dao
Em ơi vui cười lên vì đời này ai không âu lo
(I just wanna chill with you tonight)
Nếu băn khoăn ngày mai mệt nhoài hệt như con sâu đo
Em đi ra ngoài kia tìm về vài chai Strongbow-oh
Đêm nay em cần chill, việc này để cho Đen Vâu lo
Trời thả vạt nắng khiến đám tóc em hoe vàng
(Việc này để cho Đen Vâu lo)
Chiều nay đi giữa thành phố em bỗng thấy sao mơ màng
Tìm cho mình một không gian, bật bài nhạc làm em chill
Tâm hồn em phiêu dạt theo áng mây bên trời
Phiêu dạt theo áng mây bên trời
Bài hát này đã có quảng cáo
Không có tiền thì làm nhạc làm sao?'
WHERE title = N'Bài Này Chill Phết';

-- Binz
UPDATE songs 
SET lyrics_content = N'Em on top, không phải trending
Không phải YouTube, không phải trên Zing
Anh on top, em ở trên anh
Beat Touliver drop người ta gọi tên anh
Big city
Big city boi
Big city
Big city boi
Big city
Big city-Spacespeakers in da house make some mother fucker noise ay
Thả tim đầy story em (thả)
Nhắn tin đầy trong DM (slide)
Có phiền thì sorry em (sorry)
Đón, 10 giờ pm? (ten)
Yea em thích coi sea game (dô)
Hợp âm anh thích là Cm (đô)
Xe em thích BM
Việc anh thích là see em
Trói em bằng cà vạt (trói)
Penhouse trên Đà Lạt (đồi)
Nếu mà ngoan em sẽ bị thương (đôi)
Nếu mà hư em sẽ được phạt
K-r-a-z-y about u
Hay là mang thêm friend đi không sao đâu
Yea anh không thường say yes
Với em không thể say no
Nhìn anh lúc nào cũng fresh
Make them haters say wow
Big city
Big city boi
Big city
Big city boi
Big city
Big city-Spacespeakers in da house make some mother fucker noise
Shall we up all night, what u gonna do
Ngay sát DJ, what u gonna do
Them bottles keep coming, what u gonna do
Thành phố này không ngủ, tell me what u gonna do, ay
Từ lầu cao cho tới cuối ngõ
Mang chất đường phố về tận lối nhỏ
Đáy quần vẫn dưới gối oh
Rap game này anh đại diện không thể chối bỏ
Nhạc đơn giản, không phải cầu kì
Đừng hỏi anh tình yêu màu gì (không biết)
Mấy thằng ghét anh muốn spotlight
Sorry anh là cầu chì
Không lòng vòng anh như Hải Phòng (gang gang)
Thích anh rồi phải không? (gia)
Không cần vội em như Hà Nội (trưởng)
Cần thêm thời gian em mới trải lòng
Cần em như anh Cần Thơ (Cần Thơ)
Thật ra anh chỉ muốn em gần hơn (gần chút)
Thật ra anh chỉ muốn ta tương tác
Anh còn chưa ngủ, em nói ngừng mơ
Không chịu ngủ anh như Sài Gòn (Sài Gòn)
Party với bạn all night long (all night)
Nếu mà đó là, đó là thứ em muốn (tell me)
Anh có thể làm cho em, cho em hài lòng
Nghiện thuốc có thể Lào Cai (cai)
Nhưng nghiện em không thể nào cai (không cần)
Trai hư anh không phải diễn
Nhưng trai tốt anh phải vào vai
Big city
Big city boi
Big city
Big city boi
Big city
Big city-Spacespeakers in da house make some mother fucker noise
Shall we up all night, what u gonna do
Ngay sát DJ, what u gonna do
Them bottles keep coming, what u gonna do
Thành phố này không ngủ, tell me what u gonna do, ay
Big city
Big city boi
Big city
Big city boi
Big city
Big city-Spacespeakers in da house make some mother fucker noise
Shall we up all night, what u gonna do
Ngay sát DJ, what u gonna do
Them bottles keep coming, what u gonna do
Thành phố này không ngủ, tell me what u gonna do, ay'
WHERE title = N'Bigcityboi';
UPDATE songs 
SET lyrics_content = N'Ca khúc rap ngắn gọn, mạnh mẽ. Tiêu biểu: "OK..."' 
WHERE title = N'OK';

-- Jack
UPDATE songs 
SET lyrics_content = N'Thiên hà trong vũ trụ này hoài xa xôi
Riêng mình ôm gốc trời
Hạt ngọc đêm rơi rồi
Ai cũng mong hóa giàu vậy nghèo phần ai
Sai từ ngay lúc đầu tình bạc như vôi
Tiếc là mình không có nhau
Tiếc là đời quá đớn đau
Hay là duyên mộng mơ
Không như lúc đầu
Kiếp này ai đưa đón em
Kiếp này đôi tay lấm lem
Sao mà mơ trèo cao
Như Hoa Hải Đường
Vì lòng anh quá thương em
Quá yêu em nhớ thương em
Anh đợi em về
Thề rằng nếu lỡ mai này có sum vầy
Vẽ ngôi nhà bóng hình em trong đấy
Vậy mà em nỡ ra đi
Phút chia ly
Mắt hoen mi khi nào em về
Lặng nhìn ôi đống tro tàn
Đóa hoa vàng
Giữa mây ngàn chuyện tình tan
Khi em đi
Ai mang theo hành trang màu da trời
Con tim đau thương
Vương trên mi cầu mong đừng xa rời
Mới có thấy nhau hương thơm
Bay tay giang đón tay
Vậy mà giờ không thành
Người ở lại còn người không đành
Trong nhung nhớ anh đau chớ
Gương kia vỡ trái ngang duyên ta lỡ
Bỗng phút chốc xóa ngu ngơ
Anh lang thang đi trong giấc mơ
Chuyện tình mình đâu ngờ
Một hành trình một đời tôn thờ
Để giờ em đi chẳng cần nghĩ suy
Tiếc là mình không có nhau
Tiếc là đời quá đớn đau
Hay là duyên mộng mơ
Không như lúc đầu
Sau bao nhiêu năm
Con tim em mang theo cô đơn xa xăm
Kiếp này ai đưa đón em
Kiếp này đôi tay lấm lem
Sao mà mơ trèo cao như Hoa Hải Đường
Đời anh sương gió mấy
Đời em thương
Vì lòng anh quá thương em
Quá yêu em nhớ thương em
Anh đợi em về
Thề rằng nếu lỡ mai này
Có sum vầy vẽ ngôi nhà
Bóng hình em trong đấy
Vậy mà em nỡ ra đi phút chia ly
Mắt hoen mi khi nào em về
Lặng nhìn ôi đống tro tàn
Đóa hoa vàng
Giữa mây ngàn chuyện tình tan
Vì lòng anh
Vì lòng anh quá thương em
Quá yêu em nhớ thương em
Anh đợi em về
Thề rằng nếu lỡ mai này
Có sum vầy vẽ ngôi nhà
Bóng hình em trong đấy
Vậy mà em nỡ ra đi phút chia ly
Mắt hoen mi khi nào em về
Lặng nhìn ôi đống tro tàn
Đóa hoa vàng
Giữa mây ngàn chuyện tình tan
Đời anh sương gió mấy đời em ơi
Mấy đời em ơi
Mấy đời em thương' 
WHERE title = N'Hoa Hải Đường';
UPDATE songs 
SET lyrics_content = N'Em đi mất rồi, còn anh ở lại …
Người giờ còn đây không? Thuyền này còn liệu sang sông?
Buổi chiều dài mênh mông, lòng người giờ hòa hay đông
Hồng mắt em cả bầu trời đỏ hoen
Ta như đứa trẻ ngây thơ, quên đi tháng ngày ngu ngơ
Người là ngàn mây bay, mình là giọt sầu chia tay
Người cạn bầu không say, còn mình giãi bày trong đây
Này gió ơi, đừng vội vàng, lắng nghe được không?

Gió ơi xin đừng lấy em đi
Hãy mang em về chốn xuân thì
Ngày nào còn bồi hồi tóc xanh
Ngày nào còn trò chuyện vớ anh
Em nói em thương anh mà
Nói em yêu em mà
Cớ sao ta lại hóa chia xa
Đóa phong lan lặng lẽ mơ màng
Nàng dịu dàng tựa đèn phố Vinh
Đẹp rạng ngời chẳng cần cố Xinh
Hạt ngọc rơi rớt trên mái nhà, sau luống cà, và thế là …
Xa nhau, xa nhau, thôi thì nỗi nhớ hà cớ gì người mang? Woo..

Bên nhau không lâu, như là người thấy tờ giấy này nghìn trang …
Vậy hãy để màu nắng phiêu du, phiêu du trên đỉnh đầu
Và sẽ nói em nghe, em nghe, câu chuyện này là…
Cả bầu trời vàng, đỏ, tím, xanh xanh
Thuở thiếu niên thời tay nắm tay, cành lá me vàng ôm đắm say
Nhẹ nhàng lá rơi, đọng lại vấn vương ven đường

Gió ơi xin đừng lấy em đi
Hãy mang em về chốn xuân thì
Ngày nào còn bồi hồi tóc xanh
Ngày nào còn trò chuyện vớ anh
Em nói em thương anh mà
Nói em yêu em mà
Cớ sao ta lại hóa chia xa
Đóa phong lan lặng lẽ mơ màng
Nàng dịu dàng tựa đèn phố Vinh
Đẹp rạng ngời chẳng cần cố Xinh
Yêu em nhiều

Lòng này nhói đau, thương em nhiều, cạn tình biển sâu
Biển sâu anh hát
Nếu có ước muốn ngược thời gian
Nhắm mắt cố xóa dòng đời này ái phong trần vỡ tan
Đành lòng sao em xé nát tan tâm can.. họa kì thư theo bóng trăng vàng
Giá như bây giờ, giá như em ở đây

Gió ơi xin đừng lấy em đi
Hãy mang em về … về chốn xuân thì
Ngày nào còn bồi hồi tóc xanh
Ngày nào còn trò chuyện với anh
Em nói em thương anh mà
Nói em yêu em mà
Cớ sao ta lại hóa chia xa
Đóa phong lan lặng lẽ mơ màng
Nàng dịu dàng tựa đèn phố Vinh
Đẹp rạng ngời chẳng cần cố Xinh' 
WHERE title = N'Đom Đóm';

-- Hồ Ngọc Hà
UPDATE songs 
SET lyrics_content = N'Cả một trời thương nhớ
Một trời ngu ngơ, một trời ngây thơ
Cả một đời lầm lỡ
Để rồi bơ vơ, để rồi tan vỡ
Chỉ là nước mắt cứ thế rơi
Chỉ là nỗi nhớ muốn cất lời
Chỉ là những ngón tay
Không thể buông xuôi
Trong cơn mơ vẫn thấy
Người về nơi đây dịu dàng mê say
Khi cơn mơ bỗng tắt
Nhìn lại xung quanh chỉ là nước mắt
Cần một người bên em lúc này
Cần một người đan những ngón tay
Cần một bờ vai ấm cho em ngủ say
Nếu đã xem nhau như cả cuộc đời
Yêu bình yên thôi, yêu mãi không rời
Hãy ở bên nhau sánh bước chung đôi
Cùng đi đến nơi gọi là hạnh phúc
Nếu đã xem nhau như cả cuộc đời
Xin đừng buông lơi những tiếng yêu hời
Để lại một đời vấn vương
Cả một trời nhớ thương
Trong cơn mơ vẫn thấy
Người về nơi đây dịu dàng mê say
Khi cơn mơ bỗng tắt
Nhìn lại xung quanh chỉ là nước mắt
Cần một người bên em lúc này
Cần một người đan những ngón tay
Cần một bờ vai ấm cho em ngủ say
Nếu đã xem nhau như cả cuộc đời
Yêu bình yên thôi, yêu mãi không rời
Hãy ở bên nhau sánh bước chung đôi
Cùng đi đến nơi gọi là hạnh phúc
Nếu đã xem nhau như cả cuộc đời
Xin đừng buông lơi những tiếng yêu hời
Để lại một đời vấn vương
Cả một trời nhớ thương
Nếu đã xem nhau như cả cuộc đời
Yêu bình yên thôi, yêu mãi không rời
Hãy ở bên nhau sánh bước chung đôi
Cùng đi đến nơi gọi là hạnh phúc
Nếu đã xem nhau như cả cuộc đời
Xin đừng buông lơi những tiếng yêu hời
Để lại một đời vấn vương
Cả một trời nhớ thương
Để lại một đời vấn vương
Cả một trời thương nhớ
Để lại một đời vấn vương
Cả một trời nhớ thương' 
WHERE title = N'Cả Một Trời Thương Nhớ';

UPDATE songs 
SET lyrics_content = N'Trời đang tối cơn mưa phùn
Hôm nay ta hẹn nhau lần đầu tiên
Nhìn ánh mắt anh ngại ngùng
Nhẹ nhàng đặt nụ hồng lên môi em
Để trong em từng khao khát như muốn vỡ òa
Cay đắng như đã xóa nhòa
Baby you and me
Just you and me
Vì gần nhau từng hơi thở không nói nên lời
Tâm trí em đã rối bời
Baby you and me
Babe, em muốn anh đưa em về
Em muốn anh đưa em về
Em muốn anh đưa em về
Em muốn anh đưa em về
Babe, đưa em về
Đừng vụt mất yêu thương này
Bao nhiêu lâu ta đã đi tìm nhau
Đừng hoang phí thêm phút giây
Vì sao đêm nay chẳng ở lại nơi đây
Chạm nhẹ và khẽ vuốt ve bờ vai
Ghì chặt nhau đê mê
Rồi gần nhau cứ thế
Để trong em từng khao khát như muốn vỡ òa
Cay đắng như đã xóa nhòa
Baby you and me
Just you and me
Vì gần nhau từng hơi thở không nói nên lời
Tâm trí em đã rối bời
Baby you and me
Babe, em muốn anh đưa em về
Em muốn anh đưa em về
Em muốn anh đưa em về
Em muốn anh đưa em về
Babe
Để trong em từng khao khát như muốn vỡ òa
Cay đắng như đã xóa nhòa
Baby you and me
Just you and me
Vì gần nhau từng hơi thở không nói nên lời
Tâm trí em đã rối bời
Baby you and me
Babe, em muốn anh đưa em về
Em muốn anh đưa em về
Em muốn anh đưa em về
Em muốn anh đưa em về
Babe, đưa em về
You and me, you and me, you and me
You and me
You and me
You and me, you and me, you and me
You and me
Babe, đưa em về
You and me, you and me, you and me
You and me
You and me
You and me, you and me, you and me
You and me
Babe, đưa em về' 
WHERE title = N'Em Muốn Anh Đưa Em Về';

-- Noo Phước Thịnh
UPDATE songs 
SET lyrics_content = N'Giờ người đang bước bên ai không phải anh
Giờ người đang say bên ai không phải anh
Chắc do anh vô tâm
Hay tình em đã phai phôi nhạt nhoà
Giờ người bên ai anh mong em sẽ vui
Giờ người bên ai anh mong em bình yên
Để em thôi bận lòng anh sẽ ra đi vì em
Khép kín ký ức ấy vào tim
Anh khép kín những nỗi nhớ đong đầy
Để ngày dài trôi mãi anh sẽ thôi không u sầu
Cho anh thôi không nghĩ về em
Và cho anh thôi thao thức đêm dài
Giờ còn ai kề bên còn ai đêm thâu nồng say
Lối cũ ấy ta đã cùng nhau
Kề vai bên hiên trao chiếc hôn đầu
Sao giờ xa xôi quá hai đứa hai nơi phương trời
Anh hoang mang nơi đáy vực sâu
Từ khi em xa anh mang nỗi đau
Muộn màng nhưng anh biết em đã thôi không còn yêu
Giờ người đang bước bên ai không phải anh
Giờ người đang say bên ai không phải anh
Chắc do anh vô tâm
Hay tình em đã phai phôi nhạt nhoà
Giờ người bên ai anh mong em sẽ vui
Giờ người bên ai anh mong em bình yên
Để em thôi bận lòng anh sẽ ra đi vì em
Tell me babe eh eh
Why did you make me cry
Tell me babe eh eh
I miss you every night
Cause I love you love you love you love you
Cause I love you love you love you love you
Tell me babe eh eh
Why did you make me cry
Tell me babe eh eh
I miss you every night
Cause I love you love you love you love you
Cause I love you love you love you love you
Khép kín ký ức ấy vào tim
Anh khép kín những nỗi nhớ đong đầy
Để ngày dài trôi mãi anh sẽ thôi không u sầu
Cho anh thôi không nghĩ về em
Và cho anh thôi ngóng trông đêm dài
Mà thương tiếc hoài mãi nhớ đôi vai mãi mơ bên ai
Lối cũ ấy ta đã cùng nhau
Kề vai bên hiên trao chiếc hôn đầu
Sao giờ xa xôi quá hai đứa hai nơi phương trời
Anh hoang mang nơi đáy vực sâu
Từ khi em xa anh mang nỗi đau
Không anh không còn bên em mãi
Giờ người đang bước bên ai không phải anh
Giờ người đang say bên ai không phải anh
Chắc do anh vô tâm
Hay tình em đã phai phôi nhạt nhoà
Giờ người bên ai anh mong em sẽ vui
Giờ người bên ai anh mong em bình yên
Để em thôi bận lòng anh sẽ ra đi vì em
Hey baby
Why did you make me cry
Gimme the reason
Do you remember the first kiss
Your lips
Your eyes
Yo''u''re still in my mind forever
Giờ người đang bước bên ai không phải anh
Giờ người đang say bên ai không phải anh
Chắc do anh vô tâm
Hay tình em đã phai phôi nhạt nhoà
Giờ người bên ai anh mong em sẽ vui
Giờ người bên ai anh mong em bình yên
Để em thôi bận lòng anh sẽ ra đi vì em
Tell me babe eh eh
Why did you make me cry
Tell me babe eh eh
I miss you every night
Cause I love you love you love you love you
Cause I love you love you love you love you
Tell me babe eh eh
Why did you make me cry
Tell me babe eh eh
I miss you every night
Cause I love you love you love you love you
Cause I love you love you love you love you'
WHERE title = N'Cause I Love You';

UPDATE songs 
SET lyrics_content = N'Yêu em, dù là đơn phương thế thôi
Sao chẳng thể nói ra trước đôi môi kia
Thương em, là điều anh không thể ngờ
Ngăn nỗi nhớ cũng không thể ngăn trái tim
Ngần ngại chôn sâu yêu thương
Anh giấu đi tâm sự mỗi khi bên cạnh nhau
Chỉ biết lặng thinh ngắm nhìn
Một ngôi sao nhỏ bé làm tim anh mãi mong chờ
Là anh cố chấp yêu em
Dù không thể nói thành lời
Vì dại khờ anh thu mình trong suy tư của em
Dù muộn sầu hay thương nhớ anh xin một mình mang hết
Chỉ mong bờ mi em không vương chút buồn
Và nụ cười em luôn trên bờ môi
Thương em, là điều anh không thể ngờ
Ngăn nỗi nhớ cũng không thể ngăn trái tim
Ngần ngại chôn sâu yêu thương
Anh giấu đi tâm sự mỗi khi bên cạnh nhau
Chỉ biết lặng thinh ngắm nhìn
Một ngôi sao nhỏ bé làm tim anh mãi mãi mong chờ
Là anh cố chấp yêu em
Dù không thể nói thành lời
Vì dại khờ anh thu mình trong suy tư của em
Dù muộn sầu hay thương nhớ anh xin một mình mang hết
Chỉ mong bờ mi em không vương chút buồn
Và nụ cười em luôn trên bờ môi
Trọn yêu thương này trao cho em
Trọn tâm tư này anh giữ lấy
Sẽ bên cạnh em dẫu cho ngày mai
Người rời xa anh
Rời xa anh mãi
Là anh cố chấp yêu em
Là anh cố chấp yêu em
Dù không thể nói thành lời
Vì dại khờ anh thu mình trong suy tư của em
Dù muộn sầu hay thương nhớ anh xin một mình mang hết
Chỉ mong bờ mi em không vương chút buồn
Và nụ cười em luôn trên bờ môi
Chỉ mong bờ mi em không vương chút buồn
Và nụ cười em luôn trên bờ môi...' 
WHERE title = N'Thương Em Là Điều Anh Không Thể Ngờ';

-- Erik
UPDATE songs 
SET lyrics_content = N'Sau tất cả mình lại trở về với nhau
Tựa như chưa bắt đầu
Tựa như ta vừa mới quen
Sau tất cả lòng chẳng hề đổi thay
Từng ngày xa lìa khiến con tim bồi hồi
Và ta lại gần nhau hơn nữa
Có những lúc đôi ta giận hờn
Thầm trách nhau không một ai nói điều gì
Thời gian cứ chậm lại
Từng giây phút sao quá dài
Để khiến anh nhận ra mình cần em hơn
Tình yêu cứ thế đong đầy trong anh từng ngày
Vì quá yêu em nên không thể làm gì khác
Chỉ cần ta mãi luôn dành
Cho nhau những chân thành
Mọi khó khăn cũng chỉ là thử thách
Vì trái tim ta luôn luôn thuộc về nhau
Uh
Sau tất cả mình lại chung lối đi
Đoạn đường ta có nhau
Bàn tay nắm chặt bấy lâu
Sau tất cả mình cùng nhau sẻ chia
Muộn phiền không thể khiến đôi tim nhạt nhoà
Và ta lại gần nhau hơn nữa
Có những lúc đôi ta giận hờn
Thầm trách nhau không một ai nói điều gì
Thời gian cứ chậm lại
Từng giây phút sao quá dài
Để khiến anh nhận ra mình cần em hơn
Tình yêu cứ thế đong đầy trong anh từng ngày
Vì quá yêu em nên không thể làm gì khác
Chỉ cần ta mãi luôn dành cho nhau những chân thành
Mọi khó khăn cũng chỉ là thử thách
Vì trái tim ta luôn luôn thuộc về nhau
Giữ chặt bàn tay
Mình cùng nhau đi
Hết bao tháng ngày
Mọi điều gian khó ta luôn vượt qua
Để khiến ta nhận ra mình gần nhau hơn
Tình yêu cứ thế đong đầy trong anh từng ngày
Vì quá yêu em nên không thể làm gì khác
Chỉ cần ta mãi luôn dành cho nhau những chân thành
Mọi khó khăn cũng chỉ là thử thách
Vì trái tim ta luôn luôn
Thuộc về nhau
Uhm' 
WHERE title = N'Sau Tất Cả';

UPDATE songs 
SET lyrics_content = N'Không thể tin vào giây phút ấy
Không ngờ đến ngày ta chia tay
Xin lỗi anh không giữ lời hứa
Không một ai được phép...
Tổn thương lên người con gái ấy
Nhưng dù sao điều anh muốn biết
Khoảng cách nào mà ta tạo ra dấu chấm hết?
Phải nhận đau một lần mới thấu
Nếu đã là của nhau
Không giữ chặt tay sẽ vụt mất về sau
Anh thật sự ngu ngốc
Bảo vệ người ấy cũng không xong
Nỡ làm người yêu khóc
Thế thì còn xứng đáng yêu không?
Anh biết rằng anh sai
Nhưng không bao giờ tha thứ
Người nào tổn thương đến trái tim em
Như anh đã từng như thế
Anh thật lòng xin lỗi
Nhưng chẳng thể níu kéo nên thôi
Vẫn là vì anh sai
Vẫn là anh cố chấp ngày dài
Ðiều sau cuối anh làm
Nụ cười em đem hết đi
Cứ gói nỗi buồn lại để anh mang
Bao lần ta bỏ qua cho nhau
Nhưng nỗi lòng cả hai tạo ra nhiều vết xước
Hình như sau mọi lần cãi vã
Anh dần dần nhận ra ta không còn
Thân nhau yêu nhau như lúc ngày xưa
Anh thật sự ngu ngốc
Bảo vệ người ấy cũng không xong
Nỡ làm người yêu khóc
Thế thì còn xứng đáng yêu không?
Anh biết rằng anh sai
Nhưng không bao giờ tha thứ
Người nào tổn thương đến trái tim em
Như anh đã từng như thế
Anh thật lòng xin lỗi
Nhưng chẳng thể níu kéo nên thôi
Vẫn là vì anh sai
Vẫn là anh cố chấp ngày dài
Ðiều sau cuối anh làm
Nụ cười em đem hết đi
Cứ gói nỗi buồn lại để anh mang
Đành để em cứ đi như vậy
Nếu em không còn yêu anh nữa
Mỏi mệt vì tình yêu đến vậy
Chia tay để tìm người tốt hơn
Giật mình anh mới biết anh quá vô tâm
Đến ngay cả một người cũng đánh mất...
Anh thật sự ngu ngốc
Bảo vệ người ấy cũng không xong
Nỡ làm người yêu khóc
Thế thì còn xứng đáng yêu không?
Anh biết rằng anh sai
Nhưng không bao giờ tha thứ
Người làm tổn thương đến trái tim em
Như anh đã từng như thế
Anh thật lòng xin lỗi
Nhưng chẳng thể níu kéo nên thôi
Vẫn là vì anh sai
Vẫn là anh cố chấp ngày dài
Ðiều sau cuối anh làm
Nụ cười em đem hết đi
Cứ gói nỗi buồn lại để anh mang
Ðiều sau cuối anh làm
Niềm vui em hãy đem theo
Cứ gói nỗi buồn lại để anh mang' 
WHERE title = N'Em Không Sai Chúng Ta Sai';

-- Min
UPDATE songs 
SET lyrics_content = N'Từ lần đầu tiên ta đi bên nhau em đã biết tim mình đánh rơi rồi
Từ lần đầu tiên môi hôn trao nhau em đã biết không thể yêu thêm ai
Cách anh cười cong môi cách anh lặng lẽ ngồi
Ngồi nhìn bóng tối lặng thầm thời gian trôi
Người đàn ông em yêu đôi khi có những phút giây yếu đuối không ngờ (phút giây yếu đuối không ngờ)
Ngoài kia nếu có khó khăn quá về nhà anh nhé có em chờ
Có môi mềm thơm thơm có dư vị mỗi bữa cơm
Xuân hạ thu đông đều có em chờ
Yo có định mệnh nào bao nhiêu lâu anh đã ao ước (ao ước)
Ϲó vần thơ nào bao nhiêu đêm anh đi tìm hoài (tìm hoài để thấу)
Và nếu khoảng cách là một nghìn bước
Thì em chỉ cần bước một bước anh sẽ bước chín trăm chín mươi chín bước còn lại
Bước về phía anh (bước về phía anh)
Nơi mà anh thấу nắng mai (em thấу nắng mai)
Nơi con tim anh biết уên bình và mong thế thôi tha''t''s why
Tình yêu là những ánh sáng lấp lánh đèn vàng thắp lên bên ô cửa nhỏ
Tình yêu là những dịu êm từng đêm mình cùng ăn tối và nghe mưa rơi
Biết sẽ có những lúc trái tim đau đớn khôn nguôi
Vẫn yêu và yêu và yêu thế thôi
Và lần đầu tiên con tim như rụng rời lần đầu tiên anh không nói nên lời
Giấc mơ nào có đâu xa tình уêu nơi đó có hai ta
Nơi tóc em quá mượt mà con phố xưa đón đưa và
Em sẽ уêu mãi anh tháng năm dài
Bao nhiêu tháng ngàу tăm tối khi mà có em ghé qua
Nỗi buồn cũng phải buông trôi khi giờ ta có em và
Một ngàn khúc ca quanh ta xin em đừng giận anh nhé
Bởi vì đôi khi anh là ah
Người đàn ông em yêu đôi khi có những phút giây yếu đuối không ngờ (phút giây yếu đuối không ngờ)
Ngoài kia nếu có khó khăn quá về nhà anh nhé có em chờ
Có môi mềm thơm thơm có dư vị mỗi bữa cơm
Xuân hạ thu đông đều có em chờ
Tình yêu là những ánh sáng lấp lánh đèn vàng thắp lên bên ô cửa nhỏ
Tình yêu là những dịu êm từng đêm mình cùng ăn tối và nghe mưa rơi
Biết sẽ có những lúc trái tim đau đớn khôn nguôi
Vẫn yêu và yêu và yêu thế thôi
What is love, can уou feel it
What is love, can уou feel it
What is love, oh oh
What is love, tell me babу
What is love, tell me babу
What is love, oh hoh (na na na na)
Tình yêu là những ánh sáng lấp lánh đèn vàng thắp lên bên ô cửa nhỏ (yeah yeah, yeah yeah)
Tình yêu là những dịu êm từng đêm mình cùng ăn tối và nghe mưa rơi (oh woh, woh woh)
Biết sẽ có những lúc trái tim đau đớn khôn nguôi (na na na na, na na)
Vẫn yêu và yêu và yêu thế thôi (hah huh)'
WHERE title = N'Có Em Chờ';

UPDATE songs 
SET lyrics_content = N'Ngồi bên nhau một hôm trời mưa
Tí tách rơi giọt đông giọt thưa
Lặng nghe nhịp tim chầm chậm
Tăng lên biết sao cho vừa
Tình yêu là khi một người
Muốn chạm vào dịu dàng với em
Lời anh nói sao êm đềm
Nhẹ nhàng nụ hôn phớt lên môi mềm
Khi anh gọi tên em bão tố cũng hóa dịu dàng
Những thanh âm vang lên dường như phát sáng
Khi anh gọi tên em ngày mưa cũng hóa cầu vồng
Khi anh gọi tên em bình yên xoay vòng
Cầm tay nhau một hôm trời mưa
Trái tim anh rung động hay chưa
Nhẹ nhàng hôn mắt môi thơm nồng
Khoảnh khắc ấy ngỡ trời đã sang đông
Khi anh gọi tên em bão tố cũng hóa dịu dàng
Những thanh âm vang lên dường như phát sáng
Khi anh gọi tên em ngày mưa cũng hóa cầu vồng
Khi anh gọi tên em bình yên xoay vòng
Khi anh gọi tên em thanh âm phát sáng
Khi anh gọi tên em bình yên xoay vòng
Khi anh gọi tên em thanh âm phát sáng
Khi anh gọi tên em bình yên xoay vòng
Khi anh gọi tên em bình yên xoay vòng' 
WHERE title = N'Gọi Tên Em';

-- JustaTee
UPDATE songs 
SET lyrics_content = N'Forever, forever, forever
Forever alone
''I''m forever, ''I''m forever, ''I''m forever (JustaTee, just, just, just)
Một nụ cười luôn hé, thế giới vẫn quay
Còn tôi vẫn nơi đây, đưa bàn tay lên trời xanh
Ôm trọn vào lòng một khoảng trời nhỏ bé
Giữa những khát khao cơ mà chỉ tôi với riêng tôi
Đêm nằm nghe một bài ca về tình yêu, ôi chán ngắt
Lang thang một mình, cũng chẳng làm sao
Bao nhiêu lâu nay tôi đã quen rồi, quen rồi, quen một mình như thế
Yêu thêm một người, có chắc là mình sẽ good lên, hay là chỉ thêm đau đầu?
Vậy thì đành thôi forever, forever (forever alone)
Đành một mình thôi forever, forever (forever alone)
Vậy thì đành thôi forever, forever (forever alone)
Đành một mình thôi forever, forever (forever alone)
Forever (alone, alone, alone, alone, alone, alone, alone)
Forever (alone, alone, alone, alone, alone, alone, alone)
Forever (alone, alone, alone, alone, alone, alone, alone)
Forever (alone, alone, alone, alone, alone, alone, alone)
Tôi cô đơn
Một cuộc tình nữa đến, được một thời rồi đi
Những vết thương cứ thế cứ to dần làm tôi chẳng tin vào một điều gì là mãi mãi
Chỉ có ở trong phim mà thôi
Nên đừng có ai ham mộng mơ đến túp lều tranh với hai quả tim vàng
Lang thang một mình, cũng chẳng làm sao
Bao nhiêu lâu nay tôi đã quen rồi, quen rồi, quen một mình như thế
Yêu thêm một người, có chắc là mình sẽ good lên, hay là chỉ thêm đau đầu?
Vậy thì đành thôi forever, forever (forever alone)
Đành một mình thôi forever, forever (forever alone)
Vậy thì đành thôi forever, forever (forever alone)
Đành một mình thôi forever, forever (forever alone)
Forever (alone, alone, alone, alone, alone, alone, alone)
Forever (alone, alone, alone, alone, alone, alone, alone)
Forever (alone, alone, alone, alone, alone, alone, alone)
Forever (alone, alone, alone, alone, alone, alone, alone)
Tôi cô đơn
Ngoài kia đôi lứa đang say mê chung đôi vui buồn có nhau
Ngồi đây tôi hát cho riêng mỗi mình, mình vui buồn chỉ có tôi
Dừng chân đôi lúc trên con đường dài không có ai để nhớ thương
Thì ra hạnh phúc bấy lâu nay tôi tìm là được tung hoành khắp 4 phương
Đông, Tây và Nam, Bắc (tôi đi)
Chẳng cần một ai hết (tôi đi)
Cô đơn nhưng tôi chắc rằng mình luôn yêu mến cuộc sống muôn màu dù đôi khi hay cân nhắc
Gió Đông Bắc về, đôi tay lạnh ngắt
Mà tìm hơi ấm trong ngàn trái tim ngoài đó, thôi thà chúng ta cùng hát vang chung bài ca
Forever, forever (forever alone)
Đành một mình thôi forever, forever (forever alone)
Vậy thì đành thôi forever, forever (forever alone)
Đành một mình thôi forever, forever (forever alone)
Forever (alone, alone, alone, alone, alone, alone, alone)
Forever (alone, alone, alone, alone, alone, alone, alone)
Forever (alone, alone, alone, alone, alone, alone, alone)
Forever (alone, alone, alone, alone, alone, alone, alone)
Forever (alone, alone, alone, alone, alone, alone, alone)
Tôi cô đơn (alone, alone, alone, alone, alone, alone, alone)
Tôi cô đơn (alone, alone, alone, alone, alone, alone, alone)
Tôi cô đơn (alone, alone, alone, alone, alone, alone, alone)
Tôi cô đơn'
WHERE title = N'Forever Alone';

UPDATE songs 
SET lyrics_content = N'Giờ tôi lại lang thang
Tình yêu thì miên man
Ngày xanh cùng mây tung tăng tựa mình bên phím đàn
Nhìn em mình ngơ ngác
Lòng anh chợt hơi khác
Tình yêu này đến đúng lúc thấy ánh sáng vụt qua
Nụ cười tỏa hương nắng
Bình minh và mây trắng
Hình như đều kêu tôi, "Ôi thôi tình yêu đến rồi"
Chẳng ai phải thắc mắc
Còn tôi thì đã chắc
Nàng ơi nàng hãy đến chiếm lấy tâm hồn tôi
Mỉm cười lòng chợt bâng khuâng tôi chẳng biết mơ hay thật
Đợi chờ dù ngày hay đêm anh chỉ cần nghĩ cũng thấy vui
''I''m in love
Màu nắng cuốn lấp chân mây mờ xa
''I''m in love
Thành phố chỉ thấy mỗi riêng mình ta
''I''m in love
Tựa đầu bên tình yêu mới thiết tha
Chỉ crazy man fall in love
''I''m in love
Hạnh phúc chỉ hết khi anh ngừng mơ
''I''m in love
Cuộc sống vốn dĩ trôi như vần thơ
''I''m in love
Ngả lưng bên cành cây lá xác xơ
Mờ sương đưa tay anh ôm lấy em
Dẫu biết chỉ là mơ
Dẫu biết chỉ là mơ
Dẫu biết chỉ là mơ
Crazy man fall in love
Chơi vơi nơi mà loài người nhìn anh phiêu (như thằng điên)
Ừ thì đâu ai muốn là người bình thường khi yêu (yêu thằng điên)
Anh đang mơ màng về bầu trời đầy trăng với sao, em như cô tiên
Mình ca múa như hai con điên trên đồi thảo nguyên (là la lá)
Here we are, em như Beyoncé hát, hát
Here we are, anh như Jay-Z đang rap
Rap về từng ngày nắng, về từng ngày gió, về từng ngày tháng có em
But I do''n''t know who you are
Mỉm cười lòng chợt bâng khuâng tôi chẳng biết mơ hay thật
Đợi chờ dù ngày hay đêm anh chỉ cần nghĩ cũng thấy vui
''I''m in love
Màu nắng cuốn lấp chân mây mờ xa
''I''m in love
Thành phố chỉ thấy mỗi riêng mình ta
''I''m in love
Tựa đầu bên tình yêu mới thiết tha
Chỉ crazy man fall in love
''I''m in love
Hạnh phúc chỉ hết khi anh ngừng mơ
''I''m in love
Cuộc sống vốn dĩ trôi như vần thơ
''I''m in love
Ngả lưng bên cành cây lá xác xơ
Mờ sương đưa tay anh ôm lấy em
Dẫu biết chỉ là mơ
Wake up, ''I''m wake up
Thu sang rồi, em thấy không
Em đi rồi, anh cứ mong chờ
Wake up, and wake up
Tiếng vỡ tan cơn mê màng
Đánh thức nơi thiên đàng anh mơ
Nơi thiên đàng anh mơ
Nơi có em là yên bình, anh mãi như thằng si tình
Dù đôi chân anh đi mòn lối vẫn mãi không về nơi em
''I''m in love
Màu nắng cuốn lấp chân mây mờ xa
''I''m in love
Thành phố chỉ thấy mỗi riêng mình ta
''I''m in love
Tựa đầu bên tình yêu mới thiết tha
Chỉ crazy man fall in love (chỉ crazy man fall in love)
''I''m in love
Hạnh phúc chỉ hết khi anh ngừng mơ
''I''m in love
Cuộc sống vốn dĩ trôi như vần thơ
''I''m in love
Ngả lưng bên cành cây lá xác xơ
Mờ sương đưa tay anh ôm lấy em
Dẫu biết chỉ là mơ'
WHERE title = N'Thằng Điên';

-- Karik
UPDATE songs 
SET lyrics_content = N'Tôi lạc quan giữa đám đông
Nhưng khi một mình thì lại không
Cố tỏ ra là mình ổn
Nhưng sâu bên trong
Nước mắt là biển rộng
Lắm lúc chỉ muốn có ai đó
Dang tay ôm lấy tôi vào lòng
Cho tiếng cười trong mắt
Được vang vọng cô đơn
Một lần rồi khỏi những khoảng trống
Mang niềm tin phủ nắng
Nơi u uất để trời cảm xúc
Tìm về với mầm sống
Để nỗi buồn thôi bám víu màn đêm
Sương trên khoé mi
Ngày mai thôi ngừng đọng
Chỉ một lần thôi
Cho sự yếu đuối
Hôm nay thôi đợi mong
Người lạ ơi
Người đến ủi an
Tâm hồn này được không

Người lạ ơi
Xin hãy cho tôi mượn bờ vai
Tựa đầu gục ngã vì mỏi mệt quá
Người lạ ơi
Xin hãy cho tôi mượn nụ hôn
Mượn rồi tôi trả đừng vội vàng quá
Người lạ ơi
Xin người hãy ghé mua giùm tôi
Một liều quên lãng để tôi thanh thản
Người lạ ơi
Xin hãy cho tôi mượn niềm vui
Để lần yếu đuối này
Là lần cuối thôi

Cô đơn lẻ loi
Tâm tư như sóng đánh
Chơi vơi mệt mỏi
Tâm hồn thì mong manh
Không cần người phải quá sâu sắc
Chỉ cần bờ vai người đủ rộng
Chân thành đừng giấu sau màu mắt
Cùng chia sớt những nỗi sầu mênh mông
Cho trái tim yếu đuối được nghỉ ngơi
Cõi lòng hoang sơ
Hôm nay thôi dậy sóng
Một người với tôi vậy là đủ
Những thứ còn lại chẳng quan trọng
Một người không bao giờ
Nhắc về quá khứ
Không để tâm tới
Những ngày tôi ngây dại
Mở lòng bao dung
Bằng tất cả thương cảm
Dù biết chẳng thể
Cùng đi hết ngày mai
Cứ nhẹ nhàng bình yên như mây trôi
Cảm xúc không cần phải ngay lối
Lắng nghe thật khẽ cõi lòng tôi
Một người tôi cần
Lúc này chỉ vậy thôi

Người lạ ơi
Xin hãy cho tôi mượn bờ vai
Tựa đầu gục ngã vì mỏi mệt quá
Người lạ ơi
Xin hãy cho tôi mượn nụ hôn
Mượn rồi tôi trả đừng vội vàng quá
Người lạ ơi
Xin người hãy ghé mua giùm tôi
Một liều quên lãng để tôi thanh thản
Người lạ ơi
Xin hãy cho tôi mượn niềm vui
Để lần yếu đuối này
Là lần cuối thôi

Cả trời tâm tư tôi ở đấy
Vậy mà chẳng có ai hiểu
Thứ tôi mong mỏi từng ngày
Chỉ đơn giản là tình yêu
Lâu nay cả trời tâm tư tôi ở đây
Vậy mà chẳng có ai hiểu
Thứ tôi mong mỏi từng ngày
Chỉ đơn giản là tình yêu

Người lạ ơi
Xin hãy cho tôi mượn bờ vai
Tựa đầu gục ngã vì mỏi mệt quá
Người lạ ơi
Xin hãy cho tôi mượn nụ hôn
Mượn rồi tôi trả đừng vội vàng quá
Người lạ ơi
Xin người hãy ghé mua giùm tôi
Một liều quên lãng để tôi thanh thản
Người lạ ơi
Xin hãy cho tôi mượn niềm vui
Để lần yếu đuối này
Là lần cuối thôi
Người lạ ơi' 
WHERE title = N'Người Lạ Ơi';

UPDATE songs 
SET lyrics_content = N'Only C boy bánh bèo
Karik boy nhà nghèo yo
Anh này đẹp trai này đại gia này
Nhà giàu tiền tiêu thả ga xây riêng hồ bơi để nuôi cá uh uh
Còn anh thì nghèo khó tiền chẳng có
Nhà nghèo thì mẹ đâu có cho xây riêng công viên để nuôi chó
Anh kia khi yêu em thì mua cho những món đắt tiền
Luôn luôn tặng em rồi sai khiến (khiến em luôn đau đầu)
Còn anh đây khi yêu em là trao cho em con tim chung tình
Em ơi hãy lắng nghe anh này
Yêu anh đi em anh không đòi quà (là lá la la lá là)
Chia tay anh không đòi lại quà (lá la la la la là)
Anh yêu anh không đòi quà (là lá la la lá là)
Yêu em anh không đòi lại quà
Thằng kia ra đường là đại gia
Về nhà sung sướng làm đại ca
Quen em tiền bạc xài thả ga
Nhưng chia tay lại tới nhà đòi lại quà
Anh không như thế không nhiều tiền
Đồ anh xài mua ở chợ Kim Biên
Nhưng cam kết nhà có xe đạp riêng
Lỡ chia tay anh sẽ không làm phiền
Anh biết anh không được đẹp nhưng anh không vô duyên
Tài chém gió anh có nhưng mà anh không thuộc dạng bị điên
Trừ quà cáp được tính bằng hiện kim
Còn lại anh hứa gì là anh sẽ tặng liền
Boy nhà nghèo là boy nhà nghèo
Không ai thích vì chê quà bèo
Boy nhà nghèo là boy nhà nghèo
Vì boy nghèo nên không ai theo
Yêu anh đi em anh không đòi quà (là lá la la lá là)
Chia tay anh không đòi lại quà (lá la la la la là)
Anh yêu anh không đòi quà (là lá la la lá là)
Yêu em anh không đòi lại quà
Khi yêu nhau anh quan trọng lời nói
Đã nói lúc trước khi yêu ai
Sẽ không hai lời dối trá (oh baby baby)
Bao năm trôi qua và tình yêu anh trao cho em không phôi pha
Em hãy lắng nghe anh mà
Yêu anh đi em anh không đòi quà (là lá la la lá là)
Chia tay anh không đòi lại quà (lá la la la la là)
Anh yêu anh không đòi quà (là lá la la lá là)
Yêu em anh không đòi lại quà
Yêu anh đi em anh không đòi quà anh không không đòi lại đâu
Chia tay anh không đòi lại quà anh tặng quà anh không đòi lại đâu
Anh yêu anh không đòi quà anh hứa là anh không đòi lại đâu
Yêu em anh không đòi lại quà khi anh giàu anh sẽ đòi lại sau' 
WHERE title = N'Anh Không Đòi Quà';

-- Trịnh Công Sơn
UPDATE songs 
SET lyrics_content = N'Mưa vẫn mưa bay trên tầng tháp cổ
Dài tay em mấy thuở mắt xanh xao
Nghe lá thu mưa reo mòn gót nhỏ
Đường dài hun hút cho mắt thêm sâu

Mưa vẫn hay mưa trên hàng lá nhỏ
Buổi chiều ngồi ngóng những chuyến mưa qua
Trên bước chân em âm thầm lá đổ
Chợt hồn xanh buốt cho mình xót xa

Chiều nay còn mưa sao em không lại
Nhỡ mai trong cơn đau vùi
Làm sao có nhau, hằn lên nỗi đau
Bước chân em xin về mau

Mưa vẫn hay mưa cho đời biển động
Làm sao em nhớ những vết chim di
Xin hãy cho mưa qua miền đất rộng
Để người phiêu lãng quên mình lãng du

Mưa vẫn hay mưa cho đời biển động
Làm sao em biết bia đá không đau
Xin hãy cho mưa qua miền đất rộng
Ngày sau sỏi đá cũng cần có nhau.' 
WHERE title = N'Diễm Xưa';

UPDATE songs 
SET lyrics_content = N'Hạt bụi nào hoá kiếp thân tôi
Để một mai vươn hình hài lớn dậy
Ôi cát bụi tuyệt vời
Mặt trời soi một kiếp rong chơi

Hạt bụi nào hoá kiếp thân tôi
Để một mai tôi về làm cát bụi
Ôi cát bụi mệt nhoài
Tiếng động nào gõ nhịp không nguôi

Bao nhiêu năm làm kiếp con người
Chợt một chiều tóc trắng như vôi
Lá úa trên cao rụng đầy
Cho trăm năm vào chết một ngày

Mặt trời nào soi sáng tim tôi
Để tình yêu xay mòn thành đá cuội
Xin úp mặt bùi ngùi
Từng ngày qua mỏi ngóng tin vui

Cụm rừng nào lá xác xơ cây
Từ vực sâu nghe lời mời đã dậy
Ôi cát bụi phận này
Vết mực nào xoá bỏ không hay

Bao nhiêu năm làm kiếp con người
Chợt một chiều tóc trắng như vôi
Lá úa trên cao rụng đầy
Cho trăm năm vào chết một ngày

Mặt trời nào soi sáng tim tôi
Để tình yêu xay mòn thành đá cuội
Xin úp mặt bùi ngùi
Từng ngày qua mỏi ngóng tin vui

Cụm rừng nào lá xác xơ cây
Từ vực sâu nghe lời mời đã dậy
Ôi cát bụi phận này
Vết mực nào xoá bỏ không hay

Ôi cát bụi phận này
Vết mực nào xoá bỏ không hay
Ôi cát bụi phận này
Vết mực nào xoá bỏ không hay' 
WHERE title = N'Cát Bụi';

-- Đàm Vĩnh Hưng
UPDATE songs 
SET lyrics_content = N'Nửa vầng trăng đơn côi trong đêm
Buồn nhớ ai trăng rơi trên sông
Cùng sông nước trăng trôi lang thang
Đi tìm người thương
Nửa vầng trăng anh nơi phương xa
Nửa nhớ mong em đây ngóng chờ
Chờ anh đến với những nỗi nhớ
Cho tròn vầng trăng
Người ở đâu trăng em lẻ loi
Nỡ quên mau yêu thương ngày nào
Vầng trăng héo úa với tiếc thương
Cho tình vội xa
Chỉ còn em đêm ôm cô đơn
Ánh trăng non ai chia đôi vầng
Buồn rơi mãi dẫu có đớn đau
Vẫn ôm tình anh
Ngồi đây với trăng tàn lẻ loi
Lòng em nhớ anh nơi cuối trời
Dù rằng anh giờ đây phôi pha
Vui tình duyên mới
Một mình em ôm lòng đớn đau
Tìm anh dưới trăng khuya khuyết tàn
Lòng em mong rằng anh không quên
Ở nơi vắng xa em vẫn chờ
Người ở đâu trăng em lẻ loi
Nỡ quên mau yêu thương ngày nào
Vầng trăng héo úa với tiếc thương
Cho tình vội xa
Chỉ còn em đêm ôm cô đơn
Ánh trăng non ai chia đôi vầng
Buồn rơi mãi dẫu có đớn đau
Vẫn ôm tình anh
Ngồi đây với trăng tàn lẻ loi
Lòng em nhớ anh nơi cuối trời
Dù rằng anh giờ đây phôi pha
Vui tình duyên mới
Một mình em ôm lòng đớn đau
Tìm anh dưới trăng khuya khuyết tàn
Lòng em mong rằng anh không quên
Ở nơi vắng xa em vẫn chờ
Ngồi đây với trăng tàn lẻ loi
Lòng em nhớ anh nơi cuối trời
Dù rằng anh giờ đây phôi pha
Vui tình duyên mới
Một mình em ôm lòng đớn đau
Tìm anh dưới trăng khuya khuyết tàn
Lòng em mong rằng anh không quên
Ở nơi vắng
Xa em vẫn chờ' 
WHERE title = N'Nửa Vầng Trăng';

UPDATE songs 
SET lyrics_content = N'Rót mãi những chén chua cay này
Lêu bêu như gã du ca buồn
Lang thang bước với nỗi đau
Với trái tim ta tật nguyền

Buồn nào đưa ta qua những nỗi đau thương này
Giọt nồng ta say cho quên đi đôi mắt u tình
Ánh mắt đắm đuối đôi môi đam mê đôi tay buông lơi
Em yêu đã giết ta trong một đêm quên mê buồn

Vì yêu em nên ta đã hóa ngây ngô rồi
Mỗi sáng, mỗi tối, ta điên, ta say với bóng men
Đã thế những nỗi đau thương chua cay đâu không ai hay
Khi em đã bước chân theo niềm vui kia đi rồi

Nào ngờ em quay lưng cho ta quá đau buồn
Giữa quãng đời làm người tình si quá mê dại
Ôm lòng vỡ nát ...
Trút hết trong ly rượu nồng

Ðã trót đã lỡ yêu em rồi
Con tim ta lỡ trao em rồi
Ta say ta hát nghêu ngao lời tình si mê
Em có hay không nào ...

Đã trót, đã lỡ yêu em rồi
Con tim ta lỡ trao em rồi
Ta say, ta hát nghêu ngao lời tình si mê,
Em có hay không nào
' 
WHERE title = N'Say Tình';

-- Bích Phương
UPDATE songs 
SET lyrics_content = N'Năm mới lại đến em vẫn lẻ bóng một mình
Mà đâu làm sao vì em đã có gia đình
Mẹ cha chờ em về quê ăn Tết linh đình
Tay xách hành lý miệng em thì vẫn tươi xinh
Đồng quê ngày nay cũng không gì khác năm xưa
Vẫn đấy chợ phiên người ta chen chúc đẩy đưa
Về đến nhà vui làm sao kể hết cho vừa
Mẹ em ra đón hỏi ngay, "Chịu lấy chồng chưa?"
Nụ cười chợt thoáng vụt tắt trên má hồng
Em nào đâu muốn lấy chồng
Em chỉ muốn ở mãi bên mẹ cha
Mỗi năm Tết đến em mới về quê nhà
Nấu bánh mứt tặng ông bà
Giao thừa quây quần bên cả nhà ta
Đừng ai hỏi em chuyện lấy chồng
Đừng ai hỏi em chuyện lấy chồng
Mùa xuân này em chưa lấy chồng
Em vẫn chưa muốn lấy chồng!
Đầu năm cùng cha mẹ đi mừng Tết ông bà
Cô chú ai nấy đều hỏi con ế rồi à
Bạn trai đã có chưa sao chẳng dắt về nhà?
Trời ơi một câu mà ai cũng hỏi vậy ta?
Đã lấy chồng chưa?
Đã lấy chồng chưa?
Đã lấy chồng chưa?
Đã lấy chồng chưa?
Nụ cười chợt thoáng vụt tắt trên má hồng
Em nào đâu muốn lấy chồng
Em chỉ muốn ở mãi bên mẹ cha
Mỗi năm Tết đến em mới về quê nhà
Nấu bánh mứt tặng ông bà
Giao thừa quây quần bên cả nhà ta
Đừng ai hỏi em chuyện lấy chồng
Đừng ai hỏi em chuyện lấy chồng
Mùa xuân này em chưa lấy chồng
Em vẫn chưa muốn lấy chồng
La-la-la-la-la-la
La-la-la-la-la-la
La-la-la-la-la-la-la-la
La-la-la-la-la-la
La-la-la-la-la-la
La-la-la-la-la-la-la-la
Nụ cười chợt thoáng vụt tắt trên má hồng
Em nào đâu muốn lấy chồng
Em chỉ muốn ở mãi bên mẹ cha
Mỗi năm Tết đến em mới về quê nhà
Nấu bánh mứt tặng ông bà
Giao thừa quây quần bên cả nhà ta
Đừng ai hỏi em chuyện lấy chồng
Đừng ai hỏi em chuyện lấy chồng
Mùa xuân này em chưa lấy chồng
Em vẫn chưa muốn lấy chồng
La-la-la-la-la-la
La-la-la-la-la-la
La-la-la-la-la-la-la-la
La-la-la-la-la-la
La-la-la-la-la-la
La-la-la-la-la-la-la-la' 
WHERE title = N'Bao Giờ Lấy Chồng';

UPDATE songs 
SET lyrics_content = N'Lâu nay em luôn một mình
Lâu không quan tâm đến người nào
Nhưng tim em đang ồn ào
Khi anh quay sang nói lời chào
Hẹn hò ngay với em đi
Đâu có mấy khi
Sao không yêu nhau
Bây giờ yêu luôn đi
Tin không em đang thật lòng
Em nghe đây anh nói đi anh
Yêu hay không yêu
Không yêu hay yêu nói một lời
Bên nhau hay thôi
Chỉ một lời uh huh
Không yêu yêu hay không yêu
Không yêu hay yêu nói một lời thôi
Nếu anh có yêu nói đi ngại gì
Huh huh huh
Nếu anh có yêu nói đi ngại gì
Huh huh huh
Nếu anh có yêu nói đi ngại gì
Em luôn vui em hiền lành
Không hay đi chơi nấu ăn ngon
Em may em thêu thùa này
Yêu thương ai yêu hết lòng này
Chỉ là anh đấy thôi anh
Duy nhất riêng anh
Xưa nay bên em
Bao người vây xung quanh
Tin không em đang thật lòng
Em nghe đây anh nói đi anh
Yêu hay không yêu
Không yêu hay yêu nói một lời
Bên nhau hay thôi
Chỉ một lời uh huh
Không yêu yêu hay không yêu
Không yêu hay yêu nói một lời thôi
Nếu anh có yêu nói đi ngại gì
Huh huh huh
Nếu anh có yêu nói đi ngại gì
Huh huh huh
Nếu anh có yêu nói đi ngại gì
Huh huh huh
Nếu anh có yêu nói đi ngại gì
Huh huh huh
Nếu anh có yêu nói đi ngại gì
Hỡi anh có hay biết rằng
Thời gian cứ thế trôi nào có chờ
Chúng ta thì cần người ở bên
Sẻ chia những phút giây trong đời
Yêu hay không yêu
Không yêu hay yêu nói một lời
Không yêu yêu hay không yêu
Không yêu hay yêu nói một lời thôi
Nếu anh có yêu nói đi ngại gì
Huh huh huh
Nếu anh có yêu nói đi ngại gì
Huh huh huh
Nếu anh có yêu nói đi ngại gì' 
WHERE title = N'Bùa Yêu';

-- Vũ
UPDATE songs 
SET lyrics_content = N'Kìa màn đêm hiu hắt mang tên em
Quay về trong ký ức của anh qua thời gian
Chiều lặng im nghe gió đung đưa cây
Như là bao nỗi nhớ cuốn anh trôi về đâu
Này gió đừng hát
Và mang nỗi nhớ chạy đi
Quên âu lo quên hết suy tư một đời
Mưa trong anh sẽ vơi
Nhưng đôi môi đang vấn vương
Chỉ tình cờ nhìn em rồi mang theo những cơn đau thét gào
Lạ lùng em tới
Hãy tới bên anh trong chiều đông xa vắng
Mà sao giờ đây nhìn lại chẳng còn thấy em?
Lạ lùng em với...
Gió hát lên câu ca làm anh thao thức
Mà bao say mê nồng nàn giờ đã phai mau
Kìa nắng ngập tràn
Nhưng giấc mơ lại vừa bay đi
Gạt hết cuộc đời lẻ loi
Thôi mình anh, lại ngồi nhớ em
Kìa màn đêm hiu hắt mang tên em
Quay về trong kí ức của anh qua thời gian
Chiều lặng im nghe gió đung đưa cây
Như là bao nỗi nhớ cuốn anh trôi về đâu
Này gió đừng hát và mang nỗi nhớ chạy đi
Quên âu lo quên hết suy tư một đời
Mưa trong anh sẽ vơi
Nhưng đôi môi đang vấn vương
Chỉ tình cờ nhìn em rồi mang theo những cơn đau thét gào
Lạ lùng em tới
Hãy tới bên anh trong chiều đông xa vắng
Mà sao giờ đây nhìn lại chẳng còn thấy em?
Lạ lùng em với gió hát lên câu ca làm anh thao thức
Mà bao say mê nồng nàn giờ đã phai mau
Kìa nắng ngập tràn
Nhưng giấc mơ lại vừa bay đi
Gạt hết cuộc đời lẻ loi
Thôi mình anh
Lại ngồi nhớ em' 
WHERE title = N'Lạ Lùng';

UPDATE songs
SET lyrics_content = N'Chẳng phải em là hồn của cây
Mang câu hát đem đi đùa vui
Sống trong bao câu chuyện buồn
Tìm đâu đam mê say đắm ơi
Chẳng phải em là vải màu nâu
Mang hơi ấm cho anh
Ngày mưa rơi đầy
Cuối câu chuyện là đêm trắng
Mình em cô đơn
Cơn mưa chiều qua chờ ai
Cuốn theo em bỏ rơi
Tiếc đôi vai gầy
Bên phố đông
Một mùa mưa ngâu ngây ngất
Trôi đi thật nhanh là em
Ướt đôi môi lẻ loi
Tiếc thương ân tình
Em yêu mưa
Và mùa mưa ngâu
Nằm cạnh em
Chẳng phải em là hồn của cây
Mang câu hát đem đi đùa vui
Sống trong bao câu chuyện buồn
Tìm đâu đam mê say đắm ơi
Chẳng phải em là vải màu nâu
Mang hơi ấm cho anh
Ngày mưa rơi đầy
Cuối câu chuyện là đêm trắng
Mình em cô đơn
Và cơn mưa chiều qua chờ anh
Cuốn theo em bỏ rơi
Tiếc đôi vai gầy
Bên phố đông
Một mùa mưa ngâu ngây ngất
Trôi đi thật nhanh là em
Ướt đôi môi lẻ loi
Tiếc thương ân tình
Em yêu mưa
Và mùa mưa ngâu
Nằm cạnh em' 
WHERE title = N'Mùa Mưa Ngâu';

-- Amee
UPDATE songs 
SET lyrics_content = N'Anh và tôi thật ra gặp nhau và quen nhau cũng đã được mấy năm
Mà chẳng có chi hơn lời hỏi thăm
Rằng giờ này đã ăn sáng chưa?
Ở bên đấy nắng hay mưa?
Anh và tôi thật ra, uhm-hm, mải mê nhìn lén nhau
Và không một ai nói nên câu, uhm-mm
Rằng người ơi, tôi đang nhớ anh
Và anh có nhớ tôi không?
Tôi từ lâu đã thích anh rồi
Chỉ mong hai ta thành đôi
Anh nhà ở đâu thế?
Cứ tới lui trong tim tôi chẳng nhớ đường về, ah
Cứ khiến cho tôi ngày đêm phải khóc rồi cười vì nhớ một người
Khiến trái tim tôi lâu nay tương tư về anh đấy
Chỉ muốn anh có thể nghe được hết tâm tư này
Nhưng lại sợ anh từ chối
Muốn nói rồi lại thôi
Nên anh và tôi vẫn thế
Hey anh nhà ở đâu thế
Ngay từ giây đầu tiên là tôi đã biết anh thích tôi rồi
Mà tôi vờ như chẳng hay, chẳng quan tâm anh đến nửa lời
Con trai gì mà kì ghê, con trai thời này ngộ ghê
Thích người ta mà chẳng dám nói, cứ vậy thôi bước chung một đường
Anh ơi, anh nhà ở đâu tôi ở nhà kế bên
Mà sao lâu nay lâu nay lâu nay ta vẫn chưa hỏi thăm bao giờ, bao giờ
Thật là chẳng hay anh nay đã có người yêu chưa?
Vì tôi từ lâu đã thích anh rồi
Chỉ mong hai ta thành đôi
Anh nhà ở đâu thế?
Cứ tới lui trong tim tôi chẳng nhớ đường về
Cứ khiến cho tôi ngày đêm phải khóc rồi cười vì nhớ một người
Khiến trái tim tôi lâu nay tương tư về anh đấy
Chỉ muốn anh có thể nghe được hết tâm tư này
Nhưng lại sợ anh từ chối
Muốn nói rồi lại thôi
Nên anh và tôi vẫn thế
Ấp úng mấy câu thương nhau nhưng không nói gì
Nên anh và tôi vẫn thế
Vẫn chẳng thể đi bên nhau cùng chung lối về
Thật buồn ghê
Nên anh và tôi vẫn thế
Hey anh nhà ở đâu thế' 
WHERE title = N'Anh Nhà Ở Đâu Thế';

UPDATE songs 
SET lyrics_content = N'Trời đã gần sáng rồi
Mà nỗi nhớ anh vẫn còn ngổn ngang
Trời đã gần sáng rồi
Mà em vẫn ngồi hát lời thở than

Rằng anh ơi đừng rong chơi
Đừng mải mê những điều buông lời
Mà quên đi rằng trong đêm còn có người đợi anh
Anh ơi ngoài kia bao điều mặn đắng
Anh đừng lăn tăn, về nhà thôi
Trời đã gần sáng rồi!

Em đợi anh nhé
Em chờ anh nhé
Vui thôi đừng vui quá còn về với em
Kim đồng hồ vẫn từng nhịp Tik Tok
Mà sao, sao anh chưa về?

Anh đừng cứ thế
Anh đừng mãi thế anh ơi
Đừng làm trái tim này vỡ đôi
Anh đừng cứ mãi nói lời xin lỗi rồi thôi
Giờ này anh đâu rồi?

R-I-C-K-Y!
Mấy giờ rồi vậy cà? Anh taxi à! Anh taxi ơi!
Mới đi ra ngoài có 30 phút mà đã liên tục phải hắt xì hơi
Em đăng trạng thái, em up story
Em bảo là nhớ Ricky OTĐ
Nhà hàng chưa kịp đem ra món khai vị
Thì tin nhắn điện thoại kêu anh về nhà đi (Là sao?)

Khi tiệc chưa kịp tàn, đang vui cũng lũ bạn
Ngoài đường thì đông. Ôi, đường về thật gian nan
Anh sợ cái đèn đỏ; Anh chờ cái đèn xanh
Em gọi anh về liền, không là đời anh tàn canh

Anh vượt qua ngã tư ngã 5 ngã 7
Rủ rê anh ăn chơi, anh bỏ qua cả thẩy
Em quá đáng lắm luôn cử ỷ đẹp là có quyền
Nhưng mà chờ xíu đi, quẹo phải tới liền nè (Babe!)

Em đợi anh nhé
Em chờ anh nhé
Vui thôi đừng vui quá còn về với em
Kim đồng hồ vẫn từng nhịp Tik Tok
Mà sao, sao anh chưa về?

Anh đừng cứ thế
Anh đừng mãi thế anh ơi
Đừng làm trái tim này vỡ đôi
Anh đừng cứ mãi nói lời xin lỗi rồi thôi
Giờ này anh đâu rồi?

15 phút, anh còn 5 phút, anh còn 3 phút mau mau về
Về đi nhé anh về đi nhé. Nếu không, hmm nếu không thì
15 phút, anh còn 5 phút, anh còn 3 phút nhà bao việc
Tại sao cứ, em tại sao cứ ngóng trông, cứ ngóng trông anh hoài

Anh đừng cứ thế
Anh đừng mãi thế anh ơi
Đừng làm trái tim này vỡ đôi
Anh đừng cứ mãi nói lời xin lỗi rồi thôi
Giờ này anh đâu rồi?

Sao sao anh chưa về?
Mà sao anh chưa về?
' 
WHERE title = N'Sao Anh Chưa Về Nhà';

-- Soobin Hoàng Sơn
UPDATE songs 
SET lyrics_content = N'Nhiều khi anh mong
Được một lần nói ra
Hết tất cả thay vì
Ngồi lặng im nghe em
Kể về anh ta
Bằng đôi mắt lấp lánh
Đôi lúc em tránh
Ánh mắt của anh
Vì dường như lúc nào
Em cũng hiểu thấu lòng anh
Không thể ngắt lời
Càng không thể để
Giọt lệ nào được rơi
Nên anh lùi bước về sau
Để thấy em rõ hơn
Để có thể ngắm em
Từ xa âu yếm hơn
Cả nguồn sống
Bỗng chốc thu bé lại
Vừa bằng một cô gái
Hay anh vẫn sẽ
Lặng lẽ kế bên
Dù không nắm tay
Nhưng đường chung
Mãi mãi
Và từ ấy ánh mắt anh
Hồn nhiên đến lạ
Chẳng một ai có thể
Cản được trái tim
Khi đã lỡ yêu rồi
Đừng ai can ngăn tôi
Khuyên tôi buông xuôi
Vì yêu không có lỗi
Ai cũng ước muốn
Khao khát được yêu
Được chờ mong tới giờ
Ai nhắc đưa đón buổi chiều
Mỗi sáng thức dậy
Được ngắm một người
Nằm cạnh ngủ say
Nên anh lùi bước về sau
Để thấy em rõ hơn
Để có thể ngắm em
Từ xa âu yếm hơn
Cả nguồn sống
Bỗng chốc thu bé lại
Vừa bằng một cô gái
Hay anh vẫn sẽ
Lặng lẽ kế bên
Dù không nắm tay
Nhưng đường chung
Mãi mãi
Và từ ấy ánh mắt anh
Hồn nhiên đến lạ
Nên anh lùi bước về sau
Để thấy em rõ hơn
Để có thể ngắm em
Từ xa âu yếm hơn (ooh)
Cả nguồn sống (cả nguồn sống)
Bỗng chốc thu bé lại (bé lại)
Vừa bằng một cô gái oh
Hay anh vẫn sẽ lặng lẽ kế bên
Dù không nắm tay nhưng
Đường chung mãi mãi
Và từ ấy ánh mắt anh
Hồn nhiên đến lạ
Vì sao anh không thể
Gặp được em sớm hơn' 
WHERE title = N'Phía Sau Một Cô Gái';

UPDATE songs 
SET lyrics_content = N'Tôi đang ở một nơi rất xa
Nơi không có khói bụi thành phố
Ở một nơi đẹp như mơ
Trên cao êm êm mây trắng bay
Lặng nhìn biển rộng sóng vỗ-ô
Cuộc đời tôi là những chuyến đi dài
Vượt suối thác, vượt núi dốc, dù chênh vênh, có xá gì
Có biết bao thứ tươi đẹp vẫn cứ ở đó đang chờ tôi
Người xung quanh ở nơi đây thật dễ mến, dẫu mới gặp
Ánh mắt lấp lánh hiền hoà chào tôi, chào người bạn mới
Từng chặng đường dài mà ta qua
Giờ ngồi một mình lại thấy nhớ
Ngày ngày mặt trời rạng ngời vươn cao lên từ trên mái nhà
Từng chặng đường dài mà ta qua
Đều để lại kỷ niệm quý giá
Để lại một điều rằng càng đi xa ta càng thêm nhớ nhà
Đi thật xa để trở về
Đi thật xa để trở về
Có một nơi để trở về đi, đi để trở về
Tôi đang ở một nơi rất xa
Nơi không có khói bụi thành phố
Ở một nơi đẹp như mơ
Trên cao êm êm mây trắng bay
Lặng nhìn biển rộng sóng vỗ
Cuộc đời tôi là những chuyến đi dài
Vượt suối thác, vượt núi dốc, dù chênh vênh, có xá gì
Có biết bao thứ tươi đẹp vẫn cứ ở đó đang chờ tôi
Người xung quanh ở nơi đây thật dễ mến, dẫu mới gặp
Ánh mắt lấp lánh hiền hoà chào tôi, chào người bạn mới
Từng chặng đường dài mà ta qua
Giờ ngồi một mình lại thấy nhớ
Ngày ngày mặt trời rạng ngời vươn cao lên từ trên mái nhà
Từng chặng đường dài mà ta qua
Đều để lại kỷ niệm quý giá
Để lại một điều rằng càng đi xa ta càng thêm nhớ nhà
Đi thật xa để trở về
Đi thật xa để trở về
Có một nơi để trở về đi, đi để trở về
Cuộc đời thật đẹp khi được đi
Muôn nơi xa xôi rộng lớn
Nhưng ta vẫn có nơi để trở về sau mỗi chuyến đi
Điều kỳ diệu là con người ta
Đi xa hơn để trưởng thành hơn
Không quên mang theo bên cạnh hành trang nỗi nhớ gia đình
Từng chặng đường dài mà ta qua
Giờ ngồi một mình lại thấy nhớ
Ngày ngày mặt trời rạng ngời vươn cao lên từ trên mái nhà
Từng chặng đường dài mà ta qua
Đều để lại kỷ niệm quý giá
Để lại một điều rằng càng đi xa ta càng thêm nhớ nhà
Đi thật xa để trở về
Đi thật xa để trở về
Có một nơi để trở về đi, đi để trở về' 
WHERE title = N'Đi Để Trở Về';

-- Hoàng Dũng
UPDATE songs 
SET lyrics_content = N'Em, ngày em đánh rơi nụ cười vào anh
Có nghĩ sau này em sẽ chờ
Và vô tư cho đi hết những ngây thơ
Anh, một người hát mãi những điều mong manh
Lang thang tìm niềm vui đã lỡ
Chẳng buồn dặn lòng quên hết những chơ vơ
Ta yêu nhau bằng nỗi nhớ chưa khô trên những bức thư
Ta đâu bao giờ có lỗi khi không nghe tim chối từ
Chỉ tiếc rằng
Em không là nàng thơ
Anh cũng không còn là nhạc sĩ mộng mơ
Tình này nhẹ như gió
Lại trĩu lên tim ta những vết hằn
Tiếng yêu này mỏng manh
Giờ tan vỡ, thôi cũng đành
Xếp riêng những ngày tháng hồn nhiên
Trả lại...
Mai, rồi em sẽ quên ngày mình khờ dại
Mong em kỷ niệm này cất lại
Mong em ngày buồn thôi ướt đẫm trên vai
Mai, ngày em sải bước bên đời thênh thang
Chỉ cần một điều em hãy nhớ
Có một người từng yêu em tha thiết vô bờ
Em không là nàng thơ
Anh cũng không còn là nhạc sĩ mộng mơ
Tình này nhẹ như gió
Lại trĩu lên tim ta những vết hằn
Tiếng yêu này mỏng manh
Giờ tan vỡ, thôi cũng đành
Xếp riêng những ngày tháng hồn nhiên
Trả hết cho em
Em không là nàng thơ
Anh cũng không còn là nhạc sĩ mộng mơ
Tình này nhẹ như gió
Lại trĩu lên tim ta những vết hằn
Tiếng yêu này mỏng manh
Giờ tan vỡ, thôi cũng đành
Xếp riêng những ngày tháng hồn nhiên
Trả hết cho em'
WHERE title = N'Nàng Thơ';

UPDATE songs 
SET lyrics_content = N'Mây lang thang buồn trôi
Nặng mang ưu tư khát khao
Trong tim tháng ngày
Theo mưa rơi lạnh căm
Từng đêm anh nghe xót xa
Em ơi có hay
Giữa bóng tối chập chùng
Tình anh như giấc mơ
Xanh bao hi vọng
Dẫn lối bước em về
Dìu em qua đắng cay
Xua mây đen tàn nhanh
Mặt trời bừng tia nắng tươi
Lung linh ấm nồng
Khi cơn mưa vụt qua
Tình yêu đưa ta
Thoát cơn phong ba bão giông
Khuất lấp những đêm dài
Mặt trời luôn chiếu soi
Cho anh yêu đời
Xóa hết những nghi ngờ
Tình yêu như khúc ca
Từng vòng tay trao hơi ấm
Rộn rã
Đôi tim mừng vui gặp gỡ
Trong ngày mới
Nắng say tình dâng ngập lối
Nắng len qua hàng cây
Gió mơn man đùa
Lả lơi đàn bướm
Bước chân vui hạnh phúc
Nắm tay ta về
Vùng trời bình yên
Mây lang thang buồn trôi
Nặng mang ưu tư khát khao
Trong tim tháng ngày
Theo mưa rơi lạnh căm
Từng đêm anh nghe xót xa
Em ơi có hay
Giữa bóng tối chập chùng
Tình anh như giấc mơ
Xanh bao hi vọng
Dẫn lối bước em về
Dìu em qua đắng cay
Xua mây đen tàn nhanh
Mặt trời bừng tia nắng tươi
Lung linh ấm nồng
Khi cơn mưa vụt qua
Tình yêu đưa ta
Thoát cơn phong ba bão giông
Khuất lấp những đêm dài
Mặt trời luôn chiếu soi
Cho anh yêu đời
Xóa hết những nghi ngờ
Tình yêu như khúc ca
Từng vòng tay trao hơi ấm
Rộn rã
Đôi tim mừng vui gặp gỡ
Trong ngày mới
Nắng say tình dâng ngập lối
Nắng len qua hàng cây
Gió mơn man đùa
Lả lơi đàn bướm
Bước chân vui hạnh phúc
Nắm tay ta về
Vùng trời bình yên
Nắng len qua hàng cây
Gió mơn man đùa
Lả lơi đàn bướm
Bước chân vui hạnh phúc
Nắm tay ta về
Vùng trời bình yên
Nắng len qua hàng cây
Gió mơn man đùa
Lả lơi đàn bướm
Bước chân vui hạnh phúc
Nắm tay ta về
Vùng trời bình yên' 
WHERE title = N'Yên Bình';

-- Orange
UPDATE songs 
SET lyrics_content = N'Tình nhân ơi đến đây được không
Đến và khẽ ôm vào lòng
Cả thế giới bỗng nhiên như chẳng quan trọng
Tình nhân ơi anh đang ở đâu
Làm gì để gặp được nhau
Nhận ra nhau ở bên nhau tan vào nhau
Bên em anh mặc kệ tháng ngày dài trôi
Anh cứ để những nụ hôn còn ngại môi
Em lo mình sẽ mù quáng vậy cũng phải thôi
Khi yêu ta đã ngây dại rồi listen
Hy vọng trong anh màu xám đen và nâu
Là màu của men rượu cay và khói xen vào nhau
Đừng trao cho nhau lời hứa ta có giữ được đâu
Chỉ cần khi yêu ta sẽ yêu như lần đầu thôi
Cứ để anh nghe đôi mắt em không cần phải nói ra
Tình nhân ơi em đừng chỉ là thoáng qua
Thậm chí là những cuộc gọi ta cũng không cần có đâu
Anh chỉ muốn là nơi mỗi khi buồn em ghé qua
Dù chỉ là để ta chốt cửa tắt phone tóc rối khoá môi
Xiết anh bằng đôi chân vậy anh nói đến đó thôi
Căn phòng ấm dần hơi thở vang vọng
Đầu em trên vai anh những thứ khác không quan trọng
Tình nhân ơi đến đây được không
Đến và khẽ ôm vào lòng
Cả thế giới bỗng nhiên như chẳng quan trọng
Tình nhân ơi anh đang ở đâu
Làm gì để gặp được nhau
Nhận ra nhau ở bên nhau tan vào nhau
Tình nhân ơi em đang lạnh cóng
Ôm chặt em đi được không
Lần này thôi cánh tay anh hãy dang rộng
Vì em chỉ có một mình thôi
Xin hãy để môi kề môi
Để hoàng hôn chẳng lỡ hẹn với chân trời
Hãy ngủ đi em lo làm gì chuyện của sớm mai
Ai của riêng ai chẳng quan trọng nữa
Tình yêu đến cuối cùng cũng chỉ là một lời hứa thôi
Ai cũng quên rồi sao em nhớ
Bên anh đã tạnh chưa
Trời bên em vẫn mưa
Anh đã có yêu thêm ai nữa
Em thì vẫn chưa
Bởi vì để yêu được một người
Cần một giây phút thôi
Vậy mà mất cả một cuộc đời vẫn tự hỏi
Tình nhân ơi đến đây được không
Đến và khẽ ôm vào lòng
Cả thế giới bỗng nhiên như chẳng quan trọng
Tình nhân ơi anh đang ở đâu
Làm gì để gặp được nhau
Nhận ra nhau ở bên nhau tan vào nhau (tan vào nhau)
Tình nhân ơi em đang lạnh cóng
Ôm chặt em đi được không
Lần này thôi cánh tay anh hãy dang rộng
Vì em chỉ có một mình thôi
Xin hãy để môi kề môi
Để hoàng hôn chẳng lỡ hẹn với chân trời
Để cho trái tim này chẳng thể nghỉ ngơi
Tình nhân ơi đến đây được không
Đến và khẽ ôm vào lòng
Cả thế giới bỗng nhiên như chẳng quan trọng
Tình nhân ơi anh đang ở đâu
Làm gì để gặp được nhau
Nhận ra nhau ở bên nhau tan vào nhau (tan vào nhau)
Tình nhân ơi em đang lạnh cóng
Ôm chặt em đi được không
Một lần này thôi cánh tay anh hãy dang rộng
Vì em chỉ có một mình thôi
Xin hãy để môi kề môi
Để hoàng hôn chẳng lỡ hẹn với chân trời
Để cho trái tim này chẳng thể nghỉ ngơi' 
WHERE title = N'Tình Nhân Ơi';

UPDATE songs 
SET lyrics_content = N'Khi em lớn
Vui biết bao vì được đi muôn nơi
Không phải đi về nhà trước mười giờ tối
Em lớn rồi mà
Khi em lớn
Trước mắt em là bầu trời trong xanh
Em cứ vô tư chạy đi thật nhanh nào có biết rằng
Ngày em lớn
Em sẽ ngã đau hơn bây giờ
Đời đâu giống đôi vần thơ
Em nhận ra thế gian ai cũng làm ngơ
Khóc với ai bây giờ
Vì sẽ chẳng có ai lắng nghe điều không có ai muốn nghe
Em lặng thinh
Khi em lớn
Sẽ có lúc em nhận ra em yêu
Xây biết bao hy vọng đến người em ước sau này
Sẽ về một nhà
Khi em lớn
Sẽ có hơn đôi lần mà em trao
Sẽ có hơn đôi lần mà em tin
Nào có biết rằng
Ngày em lớn
Em lại lỡ tin sai một người
Lời gian dối trên vành môi em nhận ra trái tim em vỡ làm đôi
Khóc với ai bây giờ
Vì sẽ chẳng có ai lắng nghe điều không có ai muốn nghe
Em lặng thinh
Khi em lớn đường về nhà sao quá xa
Cha mẹ đây nhưng sao thật khó để nói ra con thất bại rồi
Chỉ muốn bé lại thôi
Em à
Em sẽ ngã thêm đôi ba lần
Sẽ xây xước đôi bàn chân nhưng rồi em sẽ quen thôi chớ ngại ngần
Dẫu em có một mình
Chỉ cần em vẫn luôn lắng nghe điều không có ai muốn nghe
Em cứ lắng nghe, em cứ lắng nghe' 
WHERE title = N'Khi Em Lớn';

-- Kay Trần
UPDATE songs 
SET lyrics_content = N'Tiếng chuông vang bên tai có một dòng thư gửi đến
Ngày tám tháng năm hôm nay là ngày của em đám cưới
Con tim anh rối bời, rồi đôi môi anh muốn thốt lên
Trái tim anh rụng rời (trời ơi)
Giống y như trong phim ở nhà mà tui hay coi
Là lúc MC kêu to hai người cùng trao nhẫn cưới
Cả đám đứng hát hò, còn tui như muốn vỡ tan
Tui mất em thật rồi (hai đứa nó xứng đôi quá trời)
Chú Tám đứng lên, nhạc xập xình tưng bừng
Cô Sáu kế bên cầm chung rượu mừng
Tui đứng ngẩn ngơ ở phía sau cánh gà
Nhìn em hôn người ta
Ôi biết nói sao đây, chỉ là sinh viên nghèo
Đâu dám ước mơ được em trao tình này
Duyên kiếp thế thôi, nhìn em vui bên người
Đám cưới buồn cho tui
Tiếng chuông vang bên tai có một dòng thư gửi đến
Ngày tám tháng năm hôm nay là ngày của em đám cưới
Con tim anh rối bời, rồi đôi môi anh muốn thốt lên
Trái tim anh rụng rời
Giống y như trong phim ở nhà mà tui hay coi
Là lúc MC kêu to hai người cùng trao nhẫn cưới
Cả đám đứng hát hò, còn tui như muốn vỡ tan
Tui mất em thật rồi
Chú Tám đứng lên, nhạc xập xình tưng bừng
Cô Sáu kế bên cầm chung rượu mừng
Tui đứng ngẩn ngơ ở phía sau cánh gà
Nhìn em hôn người ta
Thôi biết nói sao đây, chỉ là sinh viên nghèo
Đâu dám ước mơ được em trao tình này
Duyên kiếp thế thôi, nhìn em vui bên người
Đám cưới buồn cho tui
Thôi biết nói sao đây, chỉ là sinh viên nghèo
Đâu dám ước mơ được em trao tình này
Duyên kiếp thế thôi, nhìn em vui bên người
Đám cưới buồn cho tui' 
WHERE title = N'Chuyện Tình Tôi';

UPDATE songs 
SET lyrics_content = N'Nắm đôi bàn tay anh nhẹ nhàng ngắm em nào hay
Nắm đôi bàn tay anh nhẹ nhàng ngắm em nào hay
Good boy
Gió ngân nga âu yếm bay phiêu bồng khẽ đến nói bên tai
Hoá câu thơ say đắm mang hương nồng đánh thức nắng ban mai
Dẫn ta đi theo giấc mơ trần gian rẽ lối đến tương lai
Chốn không mưa chốn không u buồn chốn không đêm sáng như dài thêm
Ngất ngây quên ngày tháng nơi thiên đàng cứ thế mãi ngô nghê
Đoá hoa thơm nguyện ước trao cho nàng gói tất cả si mê
Những tâm tư kìm nén không nên lời nói chắc sẽ lê thê
Hồn ta vấn vương giây phút này nhớ thương từng ngày
Nắm đôi bàn tay anh nhẹ nhàng ngắm em nào hay
Bối rối rung động lên ngôi gọi mời xuyến xao trời mây
Liếc đôi hàng mi cong dịu dàng chắc yêu là đây
Nắm lấy đôi bàn tay anh này nắm đôi bàn tay anh này
Nắm đôi bàn tay anh nhẹ nhàng ngắm em nào hay
Bối rối rung động lên ngôi gọi mời xuyến xao trời mây
Liếc đôi hàng mi cong dịu dàng chắc yêu là đây
Nắm lấy đôi bàn tay anh này nắm đôi bàn tay anh này
Uh uh uh uh
Uh uh
Nắm lấy đôi bàn tay anh này nắm đôi bàn tay anh này
Bên cạnh em dù nơi đâu luôn cầm tay trong từng phút giây
Xin thời gian đừng trôi mau đưa bình yên dừng chân chốn đây
Quên phiền lo tựa vai nhau và nhìn nắng long lanh qua những tán cây
Trời xanh bỗng mơ màng trao nụ hôn giữa mây ngàn
Người mang yêu thương xua tan đi bóng tối bao vây trong anh bằng nụ cười ở trên môi
Nhờ con tim em tinh khôi đưa dẫn lối cho ta vi vu đoạn đường dài mình chung đôi
Lòng còn bồi hồi thật nhiều lời muốn nói rằng điều tuyệt vời chỉ dành riêng em thôi
Dẫu một ngày tóc phai màu anh vẫn sẽ luôn nắm chắc đôi tay này và
Nắm đôi bàn tay anh nhẹ nhàng ngắm em nào hay
Bối rối rung động lên ngôi gọi mời xuyến xao trời mây
Liếc đôi hàng mi cong dịu dàng chắc yêu là đây
Nắm lấy đôi bàn tay anh này nắm đôi bàn tay anh này
Nắm đôi bàn tay anh nhẹ nhàng ngắm em nào hay
Bối rối rung động lên ngôi gọi mời xuyến xao trời mây
Liếc đôi hàng mi cong dịu dàng chắc yêu là đây
Nắm lấy đôi bàn tay anh này nắm đôi bàn tay anh này
Uh uh uh uh
Uh uh
Nắm lấy đôi bàn tay anh này nắm đôi bàn tay anh này' 
WHERE title = N'Nắm Đôi Bàn Tay';

INSERT INTO profiles (user_id, full_name, date_of_birth, gender, phone, address, bio,
                      songs_played_count, songs_downloaded_count, artists_followed_count)
VALUES
(1,N'Admin','1990-01-01','Male','0901000001',N'HCM',N'Quản trị hệ thống',0,0,0),
(2,N'Phong Nguyễn','1995-02-02','Male','0902000002',N'Thủ Đức, HCM',N'Nghe nhạc mọi lúc',45,12,8),
(3,N'Lộc Trần','1996-03-03','Male','0903000003',N'Hà Nội',N'Fan Rap & Indie',60,20,10),
(4,N'Kha Lê','1997-04-04','Male','0904000004',N'Đà Nẵng',N'Nghe EDM',30,8,6),
(5,N'Phú Võ','1998-05-05','Male','0905000005',N'HCM',N'Thích ballad',25,6,4),
(6,N'Cường Phạm','1999-06-06','Male','0906000006',N'Hà Nội',N'Workout & playlist',50,18,9);

INSERT INTO settings (user_id, theme, language, notification_enabled, download_quality, autoplay_next, explicit_filter)
VALUES
(1,'dark','vi',1,'320kbps',1,0),
(2,'light','vi',1,'320kbps',1,0),
(3,'dark','en',1,'128kbps',1,1),
(4,'light','vi',0,'320kbps',0,0),
(5,'dark','vi',1,'320kbps',1,0),
(6,'light','en',1,'320kbps',1,1);

INSERT INTO playlists (user_id, name, is_public, description)
VALUES
(2,N'Phong - Chill Vibes',1,N'Những bài chill buổi tối'),
(2,N'Phong - Workout',1,N'Nhạc tập gym'),
(2,N'Phong - Tình Yêu',0,N'Ballad yêu thích'),
(2,N'Phong - New Releases',1,N'Bài mới theo dõi'),
(2,N'Phong - Favorites',1,N'Yêu thích nhất'),
(3,N'Loc - Rap Picks',1,N'Rap & HipHop'),
(3,N'Loc - Indie',0,N'Nhạc indie nhẹ nhàng'),
(3,N'Loc - Study',1,N'Nhạc tập trung'),
(3,N'Loc - Weekend',1,N'Những bài cuối tuần'),
(3,N'Loc - Top 50',1,N'Top nghe nhiều'),

(4,N'Kha - EDM Party',1,N'EDM sôi động'),
(4,N'Kha - Relax',0,N'Nhạc thư giãn'),
(4,N'Kha - Running',1,N'Nhạc chạy bộ'),
(4,N'Kha - Hits',1,N'Bài hit'),
(4,N'Kha - Discover',0,N'Khám phá nghệ sĩ mới'),

(5,N'Phu - Ballads',1,N'Ballad nhẹ nhàng'),
(5,N'Phu - Love Songs',1,N'Nhạc tình cảm'),
(5,N'Phu - Acoustic',0,N'Phiên bản mộc'),
(5,N'Phu - Roadtrip',1,N'Nhạc đi đường'),
(5,N'Phu - Sleep',0,N'Nhạc ru ngủ'),

(6,N'Cuong - Gym Mix',1,N'Nhạc tập mạnh'),
(6,N'Cuong - Party',1,N'Nhạc tiệc tùng'),
(6,N'Cuong - Focus',0,N'Nhạc tập trung'),
(6,N'Cuong - Classics',1,N'Những bài kinh điển'),
(6,N'Cuong - New Wave',1,N'Nhạc mới');

-- Ví dụ: gán 5 bài đầu cho Phong - Chill Vibes
INSERT INTO playlist_songs (playlist_id, song_id, order_index)
VALUES
((SELECT id FROM playlists WHERE user_id=2 AND name='Phong - Chill Vibes'), 1, 1),
((SELECT id FROM playlists WHERE user_id=2 AND name='Phong - Chill Vibes'), 3, 2),
((SELECT id FROM playlists WHERE user_id=2 AND name='Phong - Chill Vibes'), 7, 3),
((SELECT id FROM playlists WHERE user_id=2 AND name='Phong - Chill Vibes'), 9, 4),
((SELECT id FROM playlists WHERE user_id=2 AND name='Phong - Chill Vibes'), 11, 5);

-- Lặp lại cho các playlist khác (mình đưa mẫu cho một playlist mỗi user; bạn có thể copy/điền tương tự)
INSERT INTO playlist_songs (playlist_id, song_id, order_index)
VALUES
((SELECT id FROM playlists WHERE user_id=3 AND name='Loc - Rap Picks'), 3,1),
((SELECT id FROM playlists WHERE user_id=3 AND name='Loc - Rap Picks'), 4,2),
((SELECT id FROM playlists WHERE user_id=3 AND name='Loc - Rap Picks'), 10,3),
((SELECT id FROM playlists WHERE user_id=3 AND name='Loc - Rap Picks'), 11,4),
((SELECT id FROM playlists WHERE user_id=3 AND name='Loc - Rap Picks'), 12,5),

((SELECT id FROM playlists WHERE user_id=4 AND name='Kha - EDM Party'), 6,1),
((SELECT id FROM playlists WHERE user_id=4 AND name='Kha - EDM Party'), 14,2),
((SELECT id FROM playlists WHERE user_id=4 AND name='Kha - EDM Party'), 20,3),
((SELECT id FROM playlists WHERE user_id=4 AND name='Kha - EDM Party'), 16,4),
((SELECT id FROM playlists WHERE user_id=4 AND name='Kha - EDM Party'), 18,5),

((SELECT id FROM playlists WHERE user_id=5 AND name='Phu - Ballads'), 2,1),
((SELECT id FROM playlists WHERE user_id=5 AND name='Phu - Ballads'), 5,2),
((SELECT id FROM playlists WHERE user_id=5 AND name='Phu - Ballads'), 13,3),
((SELECT id FROM playlists WHERE user_id=5 AND name='Phu - Ballads'), 15,4),
((SELECT id FROM playlists WHERE user_id=5 AND name='Phu - Ballads'), 17,5),

((SELECT id FROM playlists WHERE user_id=6 AND name='Cuong - Gym Mix'), 20,1),
((SELECT id FROM playlists WHERE user_id=6 AND name='Cuong - Gym Mix'), 19,2),
((SELECT id FROM playlists WHERE user_id=6 AND name='Cuong - Gym Mix'), 10,3),
((SELECT id FROM playlists WHERE user_id=6 AND name='Cuong - Gym Mix'), 6,4),
((SELECT id FROM playlists WHERE user_id=6 AND name='Cuong - Gym Mix'), 4,5);

INSERT INTO favorites (user_id, song_id)
VALUES
(2,1),(2,3),(2,7),(2,9),(2,11),
(3,3),(3,4),(3,10),(3,11),(3,12),
(4,6),(4,14),(4,16),(4,18),(4,20),
(5,2),(5,5),(5,13),(5,15),(5,17),
(6,19),(6,20),(6,10),(6,6),(6,4);

INSERT INTO downloaded_songs (user_id, song_id, local_file_path)
VALUES
(2,1,'C:\\music\\mtp_ctcht.mp3'),(2,3,'C:\\music\\den_haitrieu.mp3'),(2,7,'C:\\music\\den_chill.mp3'),(2,9,'C:\\music\\min_coemcho.mp3'),(2,11,'C:\\music\\karik_nguoilao.mp3'),
(3,3,'C:\\music\\den_haitrieu.mp3'),(3,4,'C:\\music\\binz_bigcityboi.mp3'),(3,10,'C:\\music\\justatee_thangdien.mp3'),(3,11,'C:\\music\\karik_nguoilao.mp3'),(3,12,'C:\\music\\tcs_diemxua.mp3'),
(4,6,'C:\\music\\noo_causeiloveu.mp3'),(4,14,'C:\\music\\bichphuong_buayeu.mp3'),(4,16,'C:\\music\\amee_anhnhao.mp3'),(4,18,'C:\\music\\hoangdung_nangtho.mp3'),(4,20,'C:\\music\\kaytran_chuyentinh.mp3'),
(5,2,'C:\\music\\mytam_uocgi.mp3'),(5,5,'C:\\music\\jack_hoahaiduong.mp3'),(5,13,'C:\\music\\dvh_nuavangtrang.mp3'),(5,15,'C:\\music\\vu_lalung.mp3'),(5,17,'C:\\music\\soobin_phiasau.mp3'),
(6,19,'C:\\music\\orange_tinhnhan.mp3'),(6,20,'C:\\music\\kaytran_chuyentinh.mp3'),(6,10,'C:\\music\\justatee_forever.mp3'),(6,6,'C:\\music\\noo_thuongem.mp3'),(6,4,'C:\\music\\binz_ok.mp3');

INSERT INTO reviews (user_id, song_id, content, rating)
VALUES
(2,1,N'Bài này nghe rất đã',5),(2,3,N'Rap rất chất',5),(2,7,N'Giai điệu chill',4),(2,9,N'Lời bài hát cảm xúc',5),(2,11,N'Nghe ấm áp',4),
(3,3,N'Đen luôn có style riêng',5),(3,4,N'Beat mạnh',4),(3,10,N'Cảm xúc dâng trào',5),(3,11,N'Rất hợp gu',4),(3,12,N'Kinh điển',5),
(4,6,N'Giọng hát rất ổn',4),(4,14,N'Giai điệu bắt tai',5),(4,16,N'Nhạc trẻ vui',4),(4,18,N'Bài nhẹ nhàng',5),(4,20,N'Phù hợp party',4),
(5,2,N'Mỹ Tâm quá hay',5),(5,5,N'Jack hát cảm xúc',5),(5,13,N'Đàm Vĩnh Hưng truyền cảm',4),(5,15,N'Indie chill',4),(5,17,N'Soobin rất tình',5),
(6,19,N'Orange giọng lạ mà hay',4),(6,20,N'Kay Trần phong cách',5),(6,10,N'JustaTee deep',4),(6,6,N'Noo ballad ổn',5),(6,4,N'Binz rap chất',4);
INSERT INTO histories (user_id, song_id, duration_played)
VALUES
(2,1,240),(2,3,200),(2,7,180),(2,9,220),(2,11,210),
(3,3,230),(3,4,210),(3,10,200),(3,11,190),(3,12,300),
(4,6,250),(4,14,240),(4,16,230),(4,18,260),(4,20,220),
(5,2,260),(5,5,240),(5,13,270),(5,15,230),(5,17,250),
(6,19,240),(6,20,230),(6,10,210),(6,6,220),(6,4,200);

INSERT INTO artist_follows (user_id, artist_id)
VALUES
(2,1),(2,3),(2,7),(2,9),(2,11),
(3,3),(3,4),(3,10),(3,12),(3,13),
(4,6),(4,14),(4,16),(4,18),(4,20),
(5,2),(5,5),(5,8),(5,15),(5,17),
(6,1),(6,4),(6,9),(6,11),(6,19);
