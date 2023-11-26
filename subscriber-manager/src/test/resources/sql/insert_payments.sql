INSERT INTO payments(payment_id, external_id, payment_date, payment_channel_id, subscriber_id, amount, period, comment)
    VALUES
        (nextval('payment_id_seq'), 1, '2023-10-25', (SELECT payment_channel_id FROM payment_channels pc where pc.payment_channel_id = 1), (SELECT subscriber_id FROM subscribers sb where sb.subscriber_id = 1), 100, 'p_1', 'c_1'),
        (nextval('payment_id_seq'), 2, '2023-11-25', (SELECT payment_channel_id FROM payment_channels pc where pc.payment_channel_id = 2), (SELECT subscriber_id FROM subscribers sb where sb.subscriber_id = 2), 100, 'p_2', 'c_2');
