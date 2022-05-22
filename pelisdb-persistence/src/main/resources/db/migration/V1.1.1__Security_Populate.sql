-- USERS
INSERT INTO auth_user (`id`, `first_name`, `last_name`, `email`) VALUES (1, 'Antonio', 'Otero', 'admin@mock.es');
INSERT INTO auth_user (`id`, `first_name`, `last_name`, `email`) VALUES (2, 'John', 'Doe', 'user@mock.es');

-- ROLES
INSERT INTO auth_role (`id`, `role`) VALUES (1, 'ADMIN');
INSERT INTO auth_role (`id`, `role`) VALUES (2, 'USER');

-- USERS AND ROLES
INSERT INTO auth_user_roles (`id_user`, `id_role`) VALUES (1, 1);
INSERT INTO auth_user_roles (`id_user`, `id_role`) VALUES (2, 2);