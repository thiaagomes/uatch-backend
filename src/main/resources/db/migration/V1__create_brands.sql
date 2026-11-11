CREATE TABLE brands (
    id UUID NOT NULL,
    name TEXT NOT NULL,
    slug TEXT NOT NULL,

    CONSTRAINT pk_brands PRIMARY KEY (id),

    CONSTRAINT uk_brands_slug UNIQUE (slug),

    CONSTRAINT ck_brands_name_not_blank
        CHECK (name ~ '[^[:space:]]'),

    CONSTRAINT ck_brands_slug_format
        CHECK (slug ~ '^[a-z0-9]+(-[a-z0-9]+)*$')
);