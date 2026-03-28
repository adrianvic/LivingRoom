import readline from 'readline/promises';
import { stdin as input, stdout as output } from 'process';

const rl = readline.createInterface({ input, output });

const username = await rl.question('Please enter your username: ');
const password = await rl.question('Please enter your password: ');

rl.close();

const loginResponse = await fetch('http://localhost:8080/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username, password })
});

const data = await loginResponse.json();
if (data.success != 'true') {
  console.log(`Login not successful: ${data.message}`);
  process.exit(1);
}
const sessionID = data.sessionId;
console.log(`Your token is: ${sessionID}`);