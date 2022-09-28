# Centralised-Two-phase-locking-with-deadlock-detection

This project is an implementation of centralized rigorous 2 phase locking to perform concurrency control and resolve deadlocks through transaction rollback. All locks are managed at a central site and all other sites contact the central site to acquire or release the locks. The scope of the project is restricted to handling read, write, add and subtract operations on SQLite database. The data is completely replicated at all sites.
