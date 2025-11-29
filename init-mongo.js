// init-mongo.js
db = db.getSiblingDB('internet-store');

print("Starting database initialization...");

// Safely create collections if they don't exist
const collections = ['users', 'products', 'orders', 'addresses', 'carts'];
collections.forEach(collName => {
    if (!db.getCollectionNames().includes(collName)) {
        db.createCollection(collName);
        print(`Created collection: ${collName}`);
    } else {
        print(`Collection already exists: ${collName}`);
    }
});

// Drop existing indexes that might cause conflicts
print("Cleaning up existing indexes...");
try {
    db.products.dropIndex("name_text_description_text");
    print("Dropped old text index");
} catch (e) {
    print("Old text index doesn't exist or couldn't be dropped: " + e.message);
}

try {
    db.products.dropIndex("name_1_description_1");
    print("Dropped old compound index");
} catch (e) {
    print("Old compound index doesn't exist: " + e.message);
}

// Create indexes safely
print("Creating indexes...");

// Users indexes
try {
    db.users.createIndex({ "email": 1 }, {
        unique: true,
        name: "email_unique"
    });
    print("Created users email index");
} catch (e) {
    print("Users email index already exists or error: " + e.message);
}

// Products indexes
try {
    db.products.createIndex({
        "name": "text",
        "description": "text"
    }, {
        name: "product_text_search",
        weights: { name: 10, description: 5 }
    });
    print("Created products text search index");
} catch (e) {
    print("Products text search index already exists or error: " + e.message);
}

try {
    db.products.createIndex({ "category": 1 }, { name: "category_index" });
    print("Created products category index");
} catch (e) {
    print("Products category index already exists or error: " + e.message);
}

try {
    db.products.createIndex({ "price": 1 }, { name: "price_index" });
    print("Created products price index");
} catch (e) {
    print("Products price index already exists or error: " + e.message);
}

try {
    db.products.createIndex({ "active": 1 }, { name: "active_index" });
    print("Created products active status index");
} catch (e) {
    print("Products active status index already exists or error: " + e.message);
}

// Orders indexes
try {
    db.orders.createIndex({ "userId": 1 }, { name: "user_orders_index" });
    print("Created orders user index");
} catch (e) {
    print("Orders user index already exists or error: " + e.message);
}

try {
    db.orders.createIndex({ "status": 1 }, { name: "order_status_index" });
    print("Created orders status index");
} catch (e) {
    print("Orders status index already exists or error: " + e.message);
}

try {
    db.orders.createIndex({ "createdAt": -1 }, { name: "order_date_index" });
    print("Created orders date index");
} catch (e) {
    print("Orders date index already exists or error: " + e.message);
}

// Addresses indexes
try {
    db.addresses.createIndex({ "userId": 1 }, { name: "user_addresses_index" });
    print("Created addresses user index");
} catch (e) {
    print("Addresses user index already exists or error: " + e.message);
}

// Insert initial admin user only if it doesn't exist
print("Setting up initial data...");

const adminUser = db.users.findOne({ "email": "admin@store.com" });
if (!adminUser) {
    db.users.insertOne({
        email: "admin@store.com",
        password: "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a",
        firstName: "Admin",
        lastName: "User",
        roles: ["ROLE_ADMIN", "ROLE_USER"],
        addressIds: [],
        enabled: true,
        createdAt: new Date(),
        updatedAt: new Date()
    });
    print("Created admin user");
} else {
    print("Admin user already exists");
}

// Insert sample products only if they don't exist
const existingProducts = db.products.countDocuments();
if (existingProducts === 0) {
    db.products.insertMany([
        {
            name: "Smartphone",
            description: "Latest smartphone with advanced features",
            price: NumberDecimal("699.99"),
            stockQuantity: 50,
            category: "Electronics",
            images: ["phone1.jpg", "phone2.jpg"],
            rating: NumberDecimal("4.5"),
            reviewCount: 120,
            active: true,
            createdAt: new Date(),
            updatedAt: new Date()
        },
        {
            name: "Laptop",
            description: "High-performance laptop for work and gaming",
            price: NumberDecimal("1299.99"),
            stockQuantity: 25,
            category: "Electronics",
            images: ["laptop1.jpg", "laptop2.jpg"],
            rating: NumberDecimal("4.8"),
            reviewCount: 85,
            active: true,
            createdAt: new Date(),
            updatedAt: new Date()
        },
        {
            name: "Programming Book",
            description: "Comprehensive guide to programming",
            price: NumberDecimal("39.99"),
            stockQuantity: 100,
            category: "Books",
            images: ["book1.jpg"],
            rating: NumberDecimal("4.2"),
            reviewCount: 45,
            active: true,
            createdAt: new Date(),
            updatedAt: new Date()
        },
        {
            name: "Wireless Headphones",
            description: "Noise-cancelling wireless headphones",
            price: NumberDecimal("199.99"),
            stockQuantity: 75,
            category: "Electronics",
            images: ["headphones1.jpg"],
            rating: NumberDecimal("4.6"),
            reviewCount: 200,
            active: true,
            createdAt: new Date(),
            updatedAt: new Date()
        },
        {
            name: "T-Shirt",
            description: "Cotton t-shirt in various colors",
            price: NumberDecimal("19.99"),
            stockQuantity: 150,
            category: "Clothing",
            images: ["tshirt1.jpg"],
            rating: NumberDecimal("4.3"),
            reviewCount: 89,
            active: true,
            createdAt: new Date(),
            updatedAt: new Date()
        }
    ]);
    print("Inserted sample products");
} else {
    print("Products already exist in database");
}

// Verify data
print("Verifying data...");
print("Users count: " + db.users.countDocuments());
print("Products count: " + db.products.countDocuments());
print("Orders count: " + db.orders.countDocuments());
print("Addresses count: " + db.addresses.countDocuments());

// Show indexes
print("Current indexes:");
db.getCollectionNames().forEach(collName => {
    const indexes = db[collName].getIndexes();
    print(`Collection ${collName} has ${indexes.length} indexes`);
});

print("Database initialization completed successfully!");