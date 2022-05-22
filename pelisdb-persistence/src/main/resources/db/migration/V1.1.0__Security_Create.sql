-- DROP TABLES
DROP TABLE IF EXISTS auth_user_roles;
DROP TABLE IF EXISTS auth_role;
DROP TABLE IF EXISTS auth_user;

-- CREATE TABLES
CREATE TABLE auth_user (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(200) NOT NULL,
  email VARCHAR(70) DEFAULT NULL UNIQUE
);
CREATE INDEX idx_auth_user_email ON auth_user(email);


CREATE TABLE auth_role (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  role VARCHAR(20) NOT NULL
);

CREATE TABLE auth_user_roles (
  id_user INT NOT NULL,
  id_role INT NOT NULL,
  FOREIGN KEY(id_user) REFERENCES auth_user(id),
  FOREIGN KEY(id_role) REFERENCES auth_role(id),
  PRIMARY KEY(id_user, id_role)
);