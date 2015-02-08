# Server

Backend for service

## API

1. Insert document
Create new document curl -i -H "Content-Type: application/json" -X POST -d '{"title":"Awesome article","url":"http://github.com"}' http://localhost:3000/insert-document


2. Search Search by text curl
http://localhost:3000/search?keywords=first