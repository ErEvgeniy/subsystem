INSERT INTO subscribers(subscriber_id, external_id, firstname, patronymic, lastname, contract_number, account_number, city_id, street_id, house, flat, phone_number, email, balance, is_active, connection_date)
    VALUES
        (nextval('subscriber_id_seq'), 1, 'Ivan', 'Ivanovich', 'Ivanov', '0000001', '0000001', (SELECT city_id FROM cities ct where ct.city_id = 1), (SELECT street_id FROM streets st where st.street_id = 1), 10, 10, '+7', 't@t.com', 0.0, true, '2020-11-25'),
        (nextval('subscriber_id_seq'), 2, 'Petr', 'Petrovich', 'Petrov', '0000002', '0000002', (SELECT city_id FROM cities ct where ct.city_id = 2), (SELECT street_id FROM streets st where st.street_id = 2), 20, 20, '+7', 'q@q.com', 100.0, true, '2021-11-25');
