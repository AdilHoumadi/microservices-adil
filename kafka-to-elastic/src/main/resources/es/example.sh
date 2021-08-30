curl --location --request POST 'http://localhost:9200/twitter-index/_doc/1' \
--header 'Content-Type: application/json' \
--data-raw '{
  "userId": "1",
  "id": "1",
  "createdAt": "2020-01-01T23:00:50+0000",
  "text": "test multi word"
}'