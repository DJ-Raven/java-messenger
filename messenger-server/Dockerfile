# Use an official Node.js runtime as base image
FROM node:18.18.0


# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install application dependencies
RUN npm install

# Copy the rest of the application files to the working directory
COPY . .

# Expose port
EXPOSE 5000

# Command to start
CMD ["npm", "start"]