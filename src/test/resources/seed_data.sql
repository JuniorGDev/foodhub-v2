-- ======================================================
-- FoodHub Seed Data for H2 Test Database
-- ======================================================

-- ===========================================
-- USER TYPES
-- ===========================================
INSERT INTO user_type (id, name, created_at, updated_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222222', 'OWNER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('33333333-3333-3333-3333-333333333333', 'CUSTOMER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ===========================================
-- KITCHEN TYPES
-- ===========================================
INSERT INTO kitchen_type (id, name, created_at, updated_at)
VALUES
    ('44444444-4444-4444-4444-444444444444', 'BRAZILIAN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('55555555-5555-5555-5555-555555555555', 'ITALIAN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('66666666-6666-6666-6666-666666666666', 'JAPANESE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ===========================================
-- USERS
-- ===========================================
INSERT INTO tb_user
(
    id,
    name,
    email,
    password,
    address,
    user_type_id,
    created_at,
    updated_at
)
VALUES
    (
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'System Admin',
        'admin@foodhub.com',
        '123456',
        'São Paulo',
        '11111111-1111-1111-1111-111111111111',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        'Mario Rossi',
        'owner@foodhub.com',
        '123456',
        'São Paulo',
        '22222222-2222-2222-2222-222222222222',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'cccccccc-cccc-cccc-cccc-cccccccccccc',
        'John Customer',
        'customer@foodhub.com',
        '123456',
        'São Paulo',
        '33333333-3333-3333-3333-333333333333',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- ===========================================
-- RESTAURANTS
-- ===========================================
INSERT INTO restaurant
(
    id,
    name,
    kitchen_type_id,
    address,
    opening_time,
    closing_time,
    user_id,
    created_at,
    updated_at
)
VALUES
    (
        'dddddddd-dddd-dddd-dddd-dddddddddddd',
        'Bella Napoli',
        '55555555-5555-5555-5555-555555555555',
        'Rua Itália, 100',
        '11:00',
        '23:00',
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
        'Sushi House',
        '66666666-6666-6666-6666-666666666666',
        'Rua Japão, 50',
        '18:00',
        '23:30',
        'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- ===========================================
-- MENU ITEMS
-- ===========================================
INSERT INTO menu_item
(
    id,
    name,
    description,
    price,
    available_only_in_restaurant,
    image_path,
    restaurant_id,
    created_at,
    updated_at
)
VALUES
    (
        'ffffffff-ffff-ffff-ffff-ffffffffffff',
        'Pizza Margherita',
        'Traditional Italian pizza',
        59.90,
        false,
        '/images/pizza-margherita.png',
        'dddddddd-dddd-dddd-dddd-dddddddddddd',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '99999999-9999-9999-9999-999999999999',
        'Sushi Combo',
        '20 assorted pieces',
        89.90,
        false,
        '/images/sushi-combo.png',
        'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );
