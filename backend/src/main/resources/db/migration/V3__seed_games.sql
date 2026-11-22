-- V3: Начален каталог с игри

INSERT INTO games (title, developer, release_date, description, cover_url) VALUES
    ('The Witcher 3: Wild Hunt', 'CD Projekt Red', '2015-05-19',
        'Ловецът на чудовища Гералт търси осиновената си дъщеря в огромен отворен свят.', '/covers/witcher_3.jpg'),
    ('Elden Ring', 'FromSoftware', '2022-02-25',
        'Трудна екшън RPG игра в отворен свят, създадена съвместно с Джордж Р. Р. Мартин.', '/covers/elden_ring.jpg'),
    ('Red Dead Redemption 2', 'Rockstar Games', '2018-10-26',
        'Историята на Артър Морган и бандата на Дъч в края на Дивия запад.', '/covers/rdr2.jpg'),
    ('The Legend of Zelda: Tears of the Kingdom', 'Nintendo', '2023-05-12',
        'Линк изследва небесата и земите на Хайрул в продължението на Breath of the Wild.', '/covers/zelda_totk.jpg'),
    ('God of War Ragnarök', 'Santa Monica Studio', '2022-11-09',
        'Кратос и Атрей се изправят срещу Рагнарок в света на скандинавската митология.', '/covers/god_of_war_ragnarok.jpg'),
    ('Cyberpunk 2077', 'CD Projekt Red', '2020-12-10',
        'RPG игра в мегаполиса Найт Сити, в която играеш като наемника V.', '/covers/cyberpunk_2077.jpg'),
    ('Minecraft', 'Mojang Studios', '2011-11-18',
        'Строй, копай и оцелявай в безкраен свят от кубчета.', '/covers/minecraft.jpg'),
    ('EA Sports FC 25', 'EA Sports', '2024-09-27',
        'Футболен симулатор с лицензирани лиги, отбори и играчи.', '/covers/ea_fc_25.jpg'),
    ('Forza Horizon 5', 'Playground Games', '2021-11-09',
        'Състезания в отворен свят из пейзажите на Мексико.', '/covers/forza_horizon_5.jpg'),
    ('Resident Evil 4', 'Capcom', '2023-03-24',
        'Ремейк на класиката: Леон Кенеди спасява дъщерята на президента.', '/covers/resident_evil_4.jpg'),
    ('Mario Kart World', 'Nintendo', '2025-06-05',
        'Картинг състезания в свързан отворен свят с героите на Nintendo.', '/covers/mario_kart_world.jpg'),
    ('Sid Meier''s Civilization VI', 'Firaxis Games', '2016-10-21',
        'Походова стратегия: изгради цивилизация, която да издържи изпитанието на времето.', '/covers/civilization_6.jpg'),
    ('Portal 2', 'Valve', '2011-04-19',
        'Решавай пъзели с портали в лабораториите на Aperture Science.', '/covers/portal_2.jpg'),
    ('Counter-Strike 2', 'Valve', '2023-09-27',
        'Отборен тактически шутър: терористи срещу антитерористи.', '/covers/counter_strike_2.jpg');

-- Платформи на всяка игра
INSERT INTO game_platforms (game_id, platform_id)
SELECT g.id, p.id
FROM (VALUES
    ('The Witcher 3: Wild Hunt',                  ARRAY['PC','PlayStation 4','PlayStation 5','Xbox One','Xbox Series X|S','Nintendo Switch']),
    ('Elden Ring',                                ARRAY['PC','PlayStation 4','PlayStation 5','Xbox One','Xbox Series X|S']),
    ('Red Dead Redemption 2',                     ARRAY['PC','PlayStation 4','Xbox One']),
    ('The Legend of Zelda: Tears of the Kingdom', ARRAY['Nintendo Switch','Nintendo Switch 2']),
    ('God of War Ragnarök',                       ARRAY['PC','PlayStation 4','PlayStation 5']),
    ('Cyberpunk 2077',                            ARRAY['PC','PlayStation 4','PlayStation 5','Xbox One','Xbox Series X|S','Nintendo Switch 2']),
    ('Minecraft',                                 ARRAY['PC','PlayStation 4','PlayStation 5','Xbox One','Xbox Series X|S','Nintendo Switch']),
    ('EA Sports FC 25',                           ARRAY['PC','PlayStation 4','PlayStation 5','Xbox One','Xbox Series X|S','Nintendo Switch']),
    ('Forza Horizon 5',                           ARRAY['PC','Xbox One','Xbox Series X|S','PlayStation 5']),
    ('Resident Evil 4',                           ARRAY['PC','PlayStation 4','PlayStation 5','Xbox Series X|S']),
    ('Mario Kart World',                          ARRAY['Nintendo Switch 2']),
    ('Sid Meier''s Civilization VI',              ARRAY['PC','PlayStation 4','Xbox One','Nintendo Switch']),
    ('Portal 2',                                  ARRAY['PC','Nintendo Switch']),
    ('Counter-Strike 2',                          ARRAY['PC'])
) AS v(title, platform_names)
JOIN games g ON g.title = v.title
CROSS JOIN LATERAL unnest(v.platform_names) AS pn(name)
JOIN platforms p ON p.name = pn.name;

-- Жанрове на всяка игра
INSERT INTO game_genres (game_id, genre_id)
SELECT g.id, ge.id
FROM (VALUES
    ('The Witcher 3: Wild Hunt',                  ARRAY['RPG','Action','Adventure']),
    ('Elden Ring',                                ARRAY['RPG','Action']),
    ('Red Dead Redemption 2',                     ARRAY['Action','Adventure']),
    ('The Legend of Zelda: Tears of the Kingdom', ARRAY['Action','Adventure']),
    ('God of War Ragnarök',                       ARRAY['Action','Adventure']),
    ('Cyberpunk 2077',                            ARRAY['RPG','Shooter','Action']),
    ('Minecraft',                                 ARRAY['Adventure','Simulation']),
    ('EA Sports FC 25',                           ARRAY['Sports','Simulation']),
    ('Forza Horizon 5',                           ARRAY['Racing']),
    ('Resident Evil 4',                           ARRAY['Horror','Action','Shooter']),
    ('Mario Kart World',                          ARRAY['Racing']),
    ('Sid Meier''s Civilization VI',              ARRAY['Strategy']),
    ('Portal 2',                                  ARRAY['Puzzle','Adventure']),
    ('Counter-Strike 2',                          ARRAY['Shooter'])
) AS v(title, genre_names)
JOIN games g ON g.title = v.title
CROSS JOIN LATERAL unnest(v.genre_names) AS gn(name)
JOIN genres ge ON ge.name = gn.name;