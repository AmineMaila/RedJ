import { createClient } from 'redis'

const client = createClient({
    url: 'redis://localhost:5353'
});

client.on('error', (err) => {
    console.log('Redis err: ', err);
})

await client.connect();

await client.set('key', 'value');
const value = await client.get('key');

console.log(value);