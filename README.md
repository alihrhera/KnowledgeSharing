# Background Sync - Robust File Uploading for Android

A professional Android application demonstrating reliable background file uploading using **WorkManager**. This project implements a robust architecture to handle large file uploads by splitting them into chunks, ensuring reliability even in poor network conditions or if the app is closed.

## 🚀 Features

*   **Reliable Background Uploads**: Uses `WorkManager` to guarantee uploads complete, even if the app is killed or the device restarts.
*   **Chunked Upload Strategy**: Large files are split into smaller parts (chunks) before uploading. This allows for:
    *   **Resumability**: If an upload fails, it resumes from the last successful chunk rather than starting over.
    *   **Efficiency**: Better handling of network interruptions.
*   **Real-time Progress Reporting**: Updates upload progress to the UI and System Notifications.
*   **Upload Control**:
    *   **Pause/Resume**: Users can pause an active upload and resume it later.
    *   **Cancel**: Stop and clean up uploads.
*   **Clean Architecture**: Built with separation of concerns, making the code testable and maintainable.

## Screenshot
<p>
      <img src="res/1.png" alt="Shimmer Effect" width="200"/>
      <img src="res/2.png" alt="Shimmer Effect" width="200"/>
      <img src="res/3.png" alt="Shimmer Effect" width="200"/>
</p>

## 🛠 Tech Stack

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **Concurrency**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & Flow
*   **Background Processing**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
*   **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
*   **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture principles

## 🏗 Architecture

This project follows **Clean Architecture** principles to ensure separation of concerns and scalability.

### Layers

1.  **Domain Layer** (`domain`)
    *   **Pure Kotlin**: No Android dependencies (mostly).
    *   **Use Cases**: Contains business logic (e.g., `UploadPartsUseCase`).
    *   **Models**: Domain entities (e.g., `ItemFileInfo`, `UploadResult`).
    *   **Interfaces**: Repository definitions (e.g., `UploadRepository`).

2.  **Data Layer** (`data`)
    *   **Repositories**: Implementation of domain interfaces.
    *   **Data Sources**: Handles data retrieval and storage (e.g., `FakeApi` for simulation).

3.  **UI / Presentation Layer** (`ui`)
    *   **ViewModels**: Manages UI state and interacts with Use Cases (e.g., `UploadWorkerViewModel`).
    *   **Workers**: `UploadWorker` acts as the bridge between system background execution and domain logic.

### Upload Flow

1.  **Initiation**: The UI requests an upload via `WorkManager`.
2.  **Execution**: `UploadWorker` starts and invokes `UploadPartsUseCase`.
3.  **Processing**:
    *   The file is split into chunks using `FileSeparatorUtil`.
    *   `UploadPartsUseCase` iterates through chunks.
    *   Before each chunk, it checks for **Pause** or **Cancel** states via `UploadStateController`.
4.  **Network**: Chunks are uploaded via `UploadRepository`.
5.  **Feedback**: Progress is reported back to `UploadWorker`, which updates the Notification and `WorkManager` observers.

## 📂 Project Structure

```
hrhera.ali.backgroundsync
├── data         # Data sources and repository implementations
├── di           # Hilt dependency injection modules
├── domain       # Business logic, use cases, and interfaces
│   ├── usecases # e.g., UploadPartsUseCase
│   ├── models   # Domain models
│   └── repo     # Repository interfaces
├── ui           # UI components, ViewModels, and Workers
│   └── worker   # UploadWorker and related logic
└── util         # Utility classes (File handling, constants)
```

## 🏁 Getting Started

1.  Clone the repository.
2.  Open in **Android Studio**.
3.  Sync Gradle project.
4.  Run on an emulator or physical device.
5.  *Note: The current implementation uses a `FakeApi` to simulate network delays and success/failure. No actual server is required to test the flow.*
