# Culturebook API

## Overview

This repository holds the Ktor REST API for Culturebook, it is responsible for communicating with the database and the
application front-end. Due to the nature of the application, a scalable solution is necessary to achieve the best
results. This is achieved by breaking down the underlying system into several sub-systems starting with this repository.

**CI/CD Pipeline:**

The application has 2 environments, the development/testing environment and the release/production environment with
separate databases.
For every push to the main branch, a new development build will be deployed to Render. The deployment process of the
production environment is manual for financial reasons since each build counts towards the cloud hosting quota.

- GitHub repository and GitHub Actions. The API source code resides in a GitHub repository which in turn has several
  secret environment variables to configure the API. By using this configuration the app's tests run through GitHub
  Actions.

- Cloud hosting. After the GitHub Actions have finished testing the API, Render (the cloud hosting provider) reads the
  repository and if the testing action has passed, and by using the Dockerfile present in the root directory, starts
  building a docker container which will then be hosted on <https://api-culture-book-beta.onrender.com>.
