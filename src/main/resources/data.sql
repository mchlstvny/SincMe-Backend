-- Insert test users (password is 'password' encoded with BCrypt)
INSERT INTO users (email, first_name, last_name, password) VALUES
('test@example.com', 'Test', 'User', '$2a$10$NT9MLp8P3AYqEjU42W8mLOoX.MOzR6hr2wRfVxzGVnI.3MVVR4rKi');

-- Insert sample quotes
INSERT INTO quotes (id_quotes, content, author) VALUES
(1, 'Setiap hari adalah kesempatan baru untuk menjadi lebih baik.', 'Anonymous'),
(2, 'Kamu lebih kuat dari yang kamu pikirkan.', 'Anonymous'),
(3, 'Kesempurnaan bukan tujuan, kemajuan yang terpenting.', 'Anonymous'),
(4, 'Jangan lupa untuk tersenyum hari ini.', 'Anonymous'),
(5, 'Kamu berharga, jangan biarkan siapapun mengatakan sebaliknya.', 'Anonymous'),
(6, 'Istirahat juga bagian dari produktivitas.', 'Anonymous'),
(7, 'Fokus pada proses, bukan hanya hasil.', 'Anonymous'),
(8, 'Kegagalan adalah guru terbaik untuk kesuksesan.', 'Anonymous'),
(9, 'Berbuat baik tidak perlu menunggu sempurna.', 'Anonymous'),
(10, 'Setiap langkah kecil tetap membawamu maju.', 'Anonymous'),
(11, 'Kamu tidak perlu sempurna untuk bahagia.', 'Anonymous'),
(12, 'Kejujuran adalah hadiah terindah untuk diri sendiri.', 'Anonymous'),
(13, 'Bersyukur membuat hidup lebih bermakna.', 'Anonymous'),
(14, 'Jangan bandingkan perjalananmu dengan orang lain.', 'Anonymous'),
(15, 'Kesehatan mental sama pentingnya dengan kesehatan fisik.', 'Anonymous'),
(16, 'Kamu layak untuk merasa bahagia.', 'Anonymous'),
(17, 'Setiap orang punya cerita yang berbeda.', 'Anonymous'),
(18, 'Jangan takut untuk memulai lagi.', 'Anonymous'),
(19, 'Kebaikan kecil bisa memberi dampak besar.', 'Anonymous'),
(20, 'Percaya pada kekuatanmu sendiri.', 'Anonymous'),
(21, 'Kesabaran adalah kunci dari kesuksesan.', 'Anonymous'),
(22, 'Belajar mencintai diri sendiri adalah investasi terbaik.', 'Anonymous'),
(23, 'Setiap masalah pasti ada jalan keluarnya.', 'Anonymous'),
(24, 'Hidup adalah tentang proses belajar yang tak pernah berhenti.', 'Anonymous'),
(25, 'Keberanian dimulai dari menghadapi ketakutan.', 'Anonymous'),
(26, 'Berbagi kebahagiaan membuat hidup lebih berarti.', 'Anonymous'),
(27, 'Masa lalu adalah pelajaran, bukan tempat untuk menetap.', 'Anonymous'),
(28, 'Bersyukur membuat hidup lebih ringan.', 'Anonymous'),
(29, 'Jadilah versi terbaik dari dirimu sendiri.', 'Anonymous'),
(30, 'Setiap hari adalah kesempatan untuk mengubah hidup.', 'Anonymous');