-- Users
INSERT INTO users (id, keycloak_id, email, password, first_name, last_name, user_type, phone_number, created_at, updated_at)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'owner1@autoflex.com', 'pass', 'John', 'Doe', 'OWNER', '0700000000', now(), now()),
  ('22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'client1@autoflex.com', 'pass', 'Alice', 'Smith', 'CLIENT', '0711111111', now(), now());

-- Cars
INSERT INTO cars (
  id, owner_id, brand, model, year, transmission, fuel_type, price,
  location, description, features, images, status, rating, created_at, updated_at
) VALUES (
  '33333333-3333-3333-3333-333333333333',
  '11111111-1111-1111-1111-111111111111',
  'Tesla', 'Model 3', 2021, 'AUTOMATIC', 'ELECTRIC', 150.00,
  'Bucuresti', 'Electric car with autopilot.',
  ARRAY['AUTOPILOT', 'HEATED_SEATS'],
  ARRAY['/uploads/images/33333333-3333-3333-3333-333333333333/1.jpg',
        '/uploads/images/33333333-3333-3333-3333-333333333333/2.jpg'],
  'AVAILABLE', 4.5, now(), now()
);

-- Bookings
INSERT INTO bookings (
  id, car_id, client_id, start_date, end_date, total_price, status, created_at, updated_at
) VALUES (
  '44444444-4444-4444-4444-444444444444',
  '33333333-3333-3333-3333-333333333333',
  '22222222-2222-2222-2222-222222222222',
  '2025-04-15', '2025-04-18', 450.00, 'CONFIRMED', now(), now()
);

-- Reviews
INSERT INTO reviews (
  id, booking_id, car_id, client_id, rating, comment, created_at
) VALUES (
  '55555555-5555-5555-5555-555555555555',
  '44444444-4444-4444-4444-444444444444',
  '33333333-3333-3333-3333-333333333333',
  '22222222-2222-2222-2222-222222222222',
  5, 'Mașină excelentă, foarte curată și bine întreținută!', now()
);
