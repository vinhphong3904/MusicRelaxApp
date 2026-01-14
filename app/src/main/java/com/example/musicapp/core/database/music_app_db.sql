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
        full_name NVARCHAR(255) NOT NULL,
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

INSERT INTO users (username, email, password_hash, role, status, created_at)
VALUES
('admin','admin@mail.com','$2a$12$E3ywSQua9gCh10K643w//urAaY//Ioh3S0T16Rtfa6F8KuXp5hfbq','admin',1,SYSDATETIME()),
('phong','phong@mail.com','$2a$12$E3ywSQua9gCh10K643w//urAaY//Ioh3S0T16Rtfa6F8KuXp5hfbq','user',1,SYSDATETIME()),
('loc','loc@mail.com','$2a$12$E3ywSQua9gCh10K643w//urAaY//Ioh3S0T16Rtfa6F8KuXp5hfbq','user',1,SYSDATETIME()),
('kha','kha@mail.com','$2a$12$E3ywSQua9gCh10K643w//urAaY//Ioh3S0T16Rtfa6F8KuXp5hfbq','user',1,SYSDATETIME()),
('phu','phu@mail.com','$2a$12$E3ywSQua9gCh10K643w//urAaY//Ioh3S0T16Rtfa6F8KuXp5hfbq','user',1,SYSDATETIME()),
('cuong','cuong@mail.com','$2a$12$E3ywSQua9gCh10K643w//urAaY//Ioh3S0T16Rtfa6F8KuXp5hfbq','user',1,SYSDATETIME())

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
INSERT INTO songs (title, artist_id, album_id, genre_id, duration_seconds, audio_url)
VALUES
(N'Chúng Ta Của Hiện Tại',1,1,1,250,'/audio/mtp_ctcht.mp3'),
(N'Lạc Trôi',1,1,1,240,'/audio/mtp_lactroi.mp3'),

(N'Ước Gì',2,2,2,260,'/audio/mytam_uocgi.mp3'),
(N'Họa Mi Tóc Nâu',2,2,2,270,'/audio/mytam_hoami.mp3'),
(N'Hai Triệu Năm',3,3,3,230,'/audio/den_haitrieu.mp3'),
(N'Bài Này Chill Phết',3,3,6,220,'/audio/den_chill.mp3'),

(N'Bigcityboi',4,4,3,210,'/audio/binz_bigcityboi.mp3'),
(N'OK',4,4,3,200,'/audio/binz_ok.mp3'),

(N'Hoa Hải Đường',5,5,2,240,'/audio/jack_hoahaiduong.mp3'),
(N'Đom Đóm',5,5,1,250,'/audio/jack_domdom.mp3'),

(N'Cả Một Trời Thương Nhớ',6,6,2,260,'/audio/hoha_camot.mp3'),
(N'Em Muốn Anh Đưa Em Về',6,6,1,240,'/audio/hoha_emmuon.mp3'),

(N'Cause I Love You',7,7,1,230,'/audio/noo_causeiloveu.mp3'),
(N'Thương Em Là Điều Anh Không Thể Ngờ',7,7,1,250,'/audio/noo_thuongem.mp3'),

(N'Sau Tất Cả',8,8,2,240,'/audio/erik_sautatca.mp3'),
(N'Em Không Sai Chúng Ta Sai',8,8,2,250,'/audio/erik_emkhongsai.mp3'),

(N'Có Em Chờ',9,9,1,230,'/audio/min_coemcho.mp3'),
(N'Gọi Tên Em',9,9,1,220,'/audio/min_goitenem.mp3'),

(N'Forever Alone',10,10,3,210,'/audio/justatee_forever.mp3'),
(N'Thằng Điên',10,10,3,240,'/audio/justatee_thangdien.mp3'),

(N'Người Lạ Ơi',11,11,3,230,'/audio/karik_nguoilao.mp3'),
(N'Anh Không Đòi Quà',11,11,3,220,'/audio/karik_anhkhongdoi.mp3'),

(N'Diễm Xưa',12,12,9,300,'/audio/tcs_diemxua.mp3'),
(N'Cát Bụi',12,12,9,280,'/audio/tcs_catbui.mp3'),

(N'Nửa Vầng Trăng',13,13,2,260,'/audio/dvh_nuavangtrang.mp3'),
(N'Say Tình',13,13,2,250,'/audio/dvh_saytinh.mp3'),

(N'Bao Giờ Lấy Chồng',14,14,1,240,'/audio/bichphuong_baogio.mp3'),
(N'Bùa Yêu',14,14,1,250,'/audio/bichphuong_buayeu.mp3'),

(N'Lạ Lùng',15,15,6,230,'/audio/vu_lalung.mp3'),
(N'Mùa Mưa Ngâu',15,15,6,240,'/audio/vu_muamua.mp3'),

(N'Anh Nhà Ở Đâu Thế',16,16,1,220,'/audio/amee_anhnhao.mp3'),
(N'Sao Anh Chưa Về Nhà',16,16,1,230,'/audio/amee_saoanh.mp3'),

(N'Phía Sau Một Cô Gái',17,17,1,240,'/audio/soobin_phiasau.mp3'),
(N'Đi Để Trở Về',17,17,1,250,'/audio/soobin_didetrove.mp3'),

(N'Nàng Thơ',18,18,2,260,'/audio/hoangdung_nangtho.mp3'),
(N'Yên Bình',18,18,2,270,'/audio/hoangdung_yenbinh.mp3'),

(N'Tình Nhân Ơi',19,19,1,240,'/audio/orange_tinhnhan.mp3'),
(N'Khi Em Lớn',19,19,1,250,'/audio/orange_khiemlon.mp3'),

(N'Chuyện Tình Tôi',20,20,1,230,'/audio/kaytran_chuyentinh.mp3'),
(N'Nắm Đôi Bàn Tay',20,20,1,240,'/audio/kaytran_namdoi.mp3');

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
