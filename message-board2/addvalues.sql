INSERT INTO MB_USER (User_Name, User_Password, Join_Date)
VALUES ('Iron Man', 'jarvis2008', '2024-06-01'),
('SplashBro1', '2009Warriors', '2024-06-01'),
('Batman', 'alfred', '2024-06-09'),
('Bob', '123@Bob', '2024-06-16'),
('Hugh', 'mongus24', '2024-07-26'),
('anonymous', 'fedora32', '2024-07-10'),
('redditHater493', 'gone12ew', '2024-07-29'),
('temp', '123', '2024-08-01'),
('user', 'cs157a', '2024-08-01');


INSERT INTO COMMUNITY
VALUES ('GSW'),
('Basketball'),
('Movies'),
('San Jose'),
('DC'),
('Marvel'),
('Computer Science');


DELIMITER $$
CREATE TRIGGER MOD_MEMBER_TRIGGER
    AFTER INSERT ON COMMUNITY_MODERATOR
    FOR EACH ROW
BEGIN
    INSERT INTO COMMUNITY_MEMBERS (User_Name, Community_Name)
    VALUES(NEW.User_Name, NEW.Community_Name);
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER MEMBER_FRONT_PAGE_TRIGGER
    AFTER INSERT ON COMMUNITY_MEMBERS
    FOR EACH ROW
BEGIN
    INSERT INTO USER_FRONT_PAGE(User_Name, Community_Name)
    VALUES(NEW.User_Name, NEW.Community_Name);
END $$
DELIMITER ;


INSERT INTO COMMUNITY_MODERATOR (User_Name, Community_Name)
VALUES ('Iron Man', 'Marvel'),
('Batman', 'DC'),
('SplashBro1', 'Basketball'),
('SplashBro1', 'GSW'),
('Hugh', 'Movies'),
('user', 'San Jose'),
('user', 'Computer Science');

