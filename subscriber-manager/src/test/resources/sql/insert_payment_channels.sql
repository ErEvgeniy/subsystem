INSERT INTO payment_channels(payment_channel_id, external_id, name)
    VALUES
        (nextval('payment_channel_id_seq'), 1, 'Sberbank'),
        (nextval('payment_channel_id_seq'), 2, 'Gazprom Bank');
