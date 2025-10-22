const axios = require('axios');

async function createAdmin() {
    try {
        // First, register a new user
        console.log('Registering admin user...');
        const registerResponse = await axios.post('http://localhost:8082/api/auth/register', {
            username: 'admin',
            email: 'admin@gmail.com',
            password: 'admin123',
            phoneNumber: '1234567890'
        });

        console.log('User registered successfully:', registerResponse.data);

        // Get the user ID from response
        const userId = registerResponse.data.user.id;
        console.log('User ID:', userId);

        // Now update the role to ADMIN in database
        console.log('\nNow run this SQL command to make the user an admin:');
        console.log(`UPDATE users SET role = 'ADMIN' WHERE id = ${userId};`);

    } catch (error) {
        if (error.response) {
            console.error('Error:', error.response.data);
        } else {
            console.error('Error:', error.message);
        }
    }
}

createAdmin();

