CREATE TABLE associate (
    id             UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    des_document   VARCHAR(14) NOT NULL,
    dat_created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_associate_des_document UNIQUE (des_document)
);
