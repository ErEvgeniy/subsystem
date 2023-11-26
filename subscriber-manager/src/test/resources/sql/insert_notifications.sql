INSERT INTO notifications(notification_id, sent_date, subscriber_id, status, channel, message, destination, reason)
    VALUES
        (nextval('notification_id_seq'), '2023-10-30 11:10:00', (SELECT subscriber_id FROM subscribers sb where sb.subscriber_id = 1), 'sent', 'sms', 'hello', '+7', null),
        (nextval('notification_id_seq'), '2023-11-30 13:20:00', (SELECT subscriber_id FROM subscribers sb where sb.subscriber_id = 2), 'error', 'email', 'world', 't@t.tu', 'reason');
