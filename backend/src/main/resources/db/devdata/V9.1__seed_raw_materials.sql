INSERT INTO tb_raw_material (code, name, current_storage, min_storage, max_storage, length, width, thickness, weight_per_square_meter, active, category_id, updated_by) VALUES
('21805', 'CH A36 #1,55 1200x3000mm GALV. Z275 MP', 10, 30, 45, 1.2, 3, 1.55, 12.21, TRUE, (SELECT id FROM tb_raw_material_category WHERE name = 'Chapas Finas'), 1),
('18912', 'CH A36 #1,50 1200x3000mm MP', 19, 7, 11, 1.2, 3, 1.5, 12.21, TRUE, (SELECT id FROM tb_raw_material_category WHERE name = 'Chapas Finas'), 1),
('32641', 'CH A36 #2,00 1200x1390mm MP', 67, 25, 38, 1.2, 1.39, 2, 16, TRUE, (SELECT id FROM tb_raw_material_category WHERE name = 'Chapas Finas'), 1),
('23668', 'BARRA AÇO SAE 1020 Ø63,5mm L6000mm MP', 5, 1, 2, 6, 0, 0, 24.86, TRUE, (SELECT id FROM tb_raw_material_category WHERE name = 'Aços Longos'), 1),
('7481', 'BARRA CHATA 1"x3/16" L6000mm', 10, 20, 30, 6, 0, 0, 0.95, TRUE, (SELECT id FROM tb_raw_material_category WHERE name = 'Aços Longos'), 1),
('13298', 'BARRA CHATA 1.1/2"x3/16" L6000mm', 2, 5, 8, 6, 0, 0, 1.42, TRUE, (SELECT id FROM tb_raw_material_category WHERE name = 'Aços Longos'), 1);
