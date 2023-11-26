INSERT INTO charges(charge_id, external_id, charge_date, charge_target_id, subscriber_id, amount, period, comment)
    VALUES
        (nextval('charge_id_seq'), 1, '2023-10-25', (SELECT charge_target_id FROM charge_targets ct where ct.charge_target_id = 1), (SELECT subscriber_id FROM subscribers sb where sb.subscriber_id = 1), 100, 'p_1', 'c_1'),
        (nextval('charge_id_seq'), 2, '2023-11-25', (SELECT charge_target_id FROM charge_targets ct where ct.charge_target_id = 2), (SELECT subscriber_id FROM subscribers sb where sb.subscriber_id = 2), 100, 'p_2', 'c_2');
