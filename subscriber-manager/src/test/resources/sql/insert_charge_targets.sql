INSERT INTO charge_targets(charge_target_id, external_id, name)
    VALUES
        (nextval('charge_target_id_seq'), 1, 'Subscription fee'),
        (nextval('charge_target_id_seq'), 2, 'Penalties');
