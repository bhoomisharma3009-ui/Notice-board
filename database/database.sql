CREATE DATABASE IF NOT EXISTS notice_board_db;
USE notice_board_db;

CREATE TABLE IF NOT EXISTS notices (
id INT NOT NULL AUTO_INCREMENT,
title VARCHAR(255) NOT NULL,
category VARCHAR(50) NOT NULL,
priority VARCHAR(10) NOT NULL,
content TEXT NOT NULL,
expiry_date DATE NOT NULL,
PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO notices (id, title, category, priority, content, expiry_date) VALUES
(1, 'Annual Security Compliance Audit', 'Compliance', 'HIGH',
'Mandatory security awareness and compliance audit scheduled for all departments.', '2026-10-15'),
(2, 'Quarterly All-Hands Meeting', 'Events', 'LOW',
'Town hall meeting and operational strategy discussion in Main Auditorium.', '2026-10-20'),
(3, 'System Maintenance & Downtime Notice', 'IT & Systems', 'HIGH',
'Core network infrastructure upgrade tonight between 11 PM and 2 AM.', '2026-10-06'),
(4, 'weekly meeting', 'Administration', 'HIGH',
'Everyone is requested to meet in LF5 at 3 pm on 27th September.', '2026-10-11');
