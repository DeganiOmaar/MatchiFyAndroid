# MatchiFy Android Application

## Overview

MatchiFy is a professional talent-recruitment platform that connects talented individuals with recruiters through intelligent matching and streamlined contract management. The Android application is built using Jetpack Compose and follows the MVVM architecture pattern, providing a modern, responsive user experience for both talent seekers and recruiters.



## Architecture

The application follows the MVVM (Model-View-ViewModel) architectural pattern with clear separation of concerns:

- **Models**: Data structures representing business entities
- **Views**: Jetpack Compose UI components for presentation
- **ViewModels**: Business logic and state management
- **Data Layer**: Repository pattern for data access
- **Domain Layer**: Use cases and business logic
- **Services**: Network communication and real-time updates



### 1. Onboarding Flow

**First-Time Users**

1. **Launch Screen**: App displays splash screen
2. **Onboarding Screens**: Three-page introduction
   - Page 1: Discover opportunities based on talent
   - Page 2: Smart matching with recruiters
   - Page 3: Apply for missions effortlessly
3. **Navigation**: Previous/Next buttons with page indicators
4. **Completion**: "Start" button on final page leads to authentication

### 2. Authentication Flow

**Role Selection**

1. **Choose Role Screen**: User selects account type
   - Talent: For creators, artists, influencers, freelancers
   - Recruiter: For companies or individuals hiring talent
2. **Navigation**: Continues to appropriate signup flow

**Talent Signup**

1. **Registration Form**:
   - Full Name
   - Email Address
   - Password (with visibility toggle)
   - Confirm Password
   - Talent Categories (multi-select)
2. **Validation**: Real-time field validation
3. **Submission**: Account creation with backend
4. **Verification**: Email verification code entry
5. **Success**: Automatic login and navigation to main app

**Recruiter Signup**

1. **Registration Form**:
   - Full Name
   - Company Name
   - Email Address
   - Password (with visibility toggle)
   - Confirm Password
2. **Validation**: Real-time field validation
3. **Submission**: Account creation with backend
4. **Verification**: Email verification code entry
5. **Success**: Automatic login and navigation to main app

**Login**

1. **Login Screen**:
   - Email input field
   - Password input field (with show/hide toggle)
   - Remember Me checkbox
   - Reset Password link
2. **Authentication**: Credential verification
3. **Session Management**: Token storage and persistence
4. **Navigation**: Role-based redirect to main interface

**Password Recovery**

1. **Forgot Password**: Email submission
2. **Verification Code**: Code entry from email
3. **Reset Password**: New password creation
4. **Confirmation**: Success message and redirect to login

### 3. Main Application Flow

**Tab Navigation**

The main interface uses a bottom navigation bar with four primary sections:

1. **Missions Tab**: Browse and manage missions
2. **Proposals Tab**: View and manage proposals
3. **Messages Tab**: Conversation and messaging
4. **Alerts Tab**: Notifications and updates

### 4. Missions Flow

**For Talent Users**

1. **Mission List Screen**:
   - Search bar for filtering missions
   - Tab navigation:
     - Most Recent: Latest posted missions
     - Best Matches: AI-recommended missions based on profile
     - Favorites: Saved missions
   - Mission cards displaying:
     - Mission title
     - Budget/price
     - Skills required
     - Posted time
     - Proposal count
     - Favorite toggle

2. **Mission Details**:
   - Complete mission description
   - Budget information
   - Required skills and expertise
   - Activity metrics (proposals, interviewing count)
   - AI Fit Analysis button
   - Apply button (if not already applied)

3. **AI Fit Analysis**:
   - Match score percentage
   - Radar chart visualization
   - Detailed reasoning for match
   - Skill alignment analysis

4. **Create Proposal**:
   - Cover letter/message
   - Proposed rate
   - Estimated timeline
   - Submission confirmation

**For Recruiter Users**

1. **Mission List Screen**:
   - Search bar for filtering
   - Create mission button (+ icon)
   - Mission cards with owner actions:
     - Edit mission
     - Delete mission
     - View proposals

2. **Create Mission**:
   - Mission title
   - Description
   - Budget
   - Required skills (multi-select)
   - Duration
   - Submission and validation

3. **Edit Mission**:
   - Modify existing mission details
   - Update and save changes

4. **Mission Proposals View**:
   - List of all proposals for selected mission
   - AI sorting toggle for intelligent ranking
   - Proposal cards showing:
     - Talent name
     - AI match score (when enabled)
     - Proposal message preview
     - Status badge

### 5. Proposals Flow

**For Talent Users**

1. **Proposals List**:
   - Tab navigation:
     - Active: Current proposals
     - Archived: Past proposals
   - Status filtering (Active tab):
     - All
     - Not Viewed
     - Viewed
     - Accepted
     - Refused
   - Swipe actions:
     - Archive proposal
     - Delete proposal

2. **Proposal Details**:
   - Full proposal message
   - Mission information
   - Status updates
   - Timeline information

**For Recruiter Users**

1. **Mission Selection**:
   - Dropdown to select mission
   - Unviewed proposal count badges
   - AI sorting toggle

2. **Proposals List**:
   - Talent name and profile
   - AI match score (when AI sorting enabled)
   - Proposal preview
   - Status indicators

3. **Proposal Details**:
   - Complete proposal information
   - Talent profile link
   - Accept/Reject actions
   - Status management

### 6. Contracts Flow

**Contract Creation (Recruiter)**

1. **Initiate Contract**:
   - Access from conversation with talent
   - "Send Contract" button in message interface

2. **Contract Form**:
   - Contract title
   - Contract content/terms
   - Payment details
   - Start date
   - End date
   - Signature pad for recruiter signature

3. **Contract Submission**:
   - Validation of all required fields
   - PDF generation
   - Automatic message sent to talent with contract

**Contract Review and Signing (Talent)**

1. **Contract Notification**:
   - Contract message appears in conversation
   - Contract icon and status indicator

2. **Review Contract**:
   - View contract title and content
   - Review payment details and dates
   - View recruiter signature
   - Decline or Sign options

3. **Sign Contract**:
   - Digital signature pad
   - Signature capture
   - Send to recruiter button

4. **Contract Completion**:
   - Success confirmation
   - Updated contract status
   - Both parties can view signed PDF

**Contract Management**

- View all contracts in conversation history
- Download signed PDF documents
- Status tracking:
  - Pending (awaiting talent signature)
  - Signed by Both (completed)
  - Declined

### 7. Messaging Flow

**Conversations List**

1. **Conversation Cards**:
   - Other user's profile picture
   - Name
   - Last message preview
   - Timestamp
   - Unread indicator

2. **Filtering and Search**:
   - Search conversations
   - Sort by recent activity

**Conversation View**

1. **Message Interface**:
   - Header with user info and back button
   - Scrollable message history
   - Message bubbles (sent/received styling)
   - Timestamp for each message

2. **Sending Messages**:
   - Text input field
   - Send button (disabled when empty)
   - Real-time message delivery

3. **Contract Messages**:
   - Special contract message cards
   - Contract icon and title
   - Status indicator (signed/pending)
   - Tap to view/review contract

4. **Recruiter Features**:
   - "Send Contract" button
   - Contract creation from conversation

### 8. Profile Flow

**Talent Profile**

1. **Profile Display**:
   - Banner image
   - Profile picture
   - Full name
   - Email address
   - Talent categories (badges)
   - Description/bio
   - Skills section
   - Portfolio projects
   - CV attachment
   - AI Profile Insights

2. **Profile Actions** (More menu):
   - Edit Profile
   - Manage Portfolio
   - Attach CV
   - Settings

3. **Edit Profile**:
   - Update personal information
   - Change profile picture
   - Modify description
   - Update talent categories
   - Manage skills

4. **Portfolio Management**:
   - Grid/List view toggle
   - Add new projects
   - Edit existing projects
   - Delete projects
   - Project details:
     - Title
     - Description
     - Media (images/videos)
     - Links

5. **AI Profile Insights**:
   - Profile completeness score
   - AI-generated recommendations
   - Skill gap analysis
   - Profile optimization suggestions

**Recruiter Profile**

1. **Profile Display**:
   - Banner image
   - Profile picture
   - Full name
   - Company name
   - Email address
   - Description/bio

2. **Profile Actions**:
   - Edit Profile
   - Settings

3. **Edit Profile**:
   - Update personal information
   - Change profile picture
   - Modify company details
   - Update description

### 9. Portfolio Flow

**Portfolio List**

1. **View Modes**:
   - Grid view: Visual thumbnail layout
   - List view: Detailed project cards

2. **Project Cards**:
   - Project thumbnail
   - Title
   - Description preview
   - Media count indicator

3. **Actions**:
   - Add new project
   - Edit project
   - Delete project
   - View project details

**Add/Edit Project**

1. **Project Form**:
   - Project title
   - Description
   - Media upload (images/videos/PDFs)
   - External links
   - Tags/categories

2. **Media Management**:
   - Photo library picker
   - Video picker
   - PDF document picker
   - Media preview thumbnails
   - Remove media option

3. **Submission**:
   - Validation
   - Upload to backend
   - Success confirmation

**Project Details**

1. **Full Project View**:
   - Large media display
   - Complete description
   - All project links
   - Media gallery
   - Edit/Delete options

### 10. Alerts Flow

**Alerts List**

1. **Alert Cards**:
   - Alert icon
   - Title
   - Message preview
   - Timestamp
   - Read/unread indicator

2. **Alert Types**:
   - Proposal updates
   - Mission notifications
   - Contract status changes
   - System announcements

3. **Actions**:
   - Tap to view details
   - Mark as read
   - Navigate to related content

### 11. Statistics Flow

**Stats Dashboard**

1. **Talent Statistics**:
   - Total proposals submitted
   - Accepted proposals
   - Active contracts
   - Profile views
   - Success rate

2. **Recruiter Statistics**:
   - Total missions posted
   - Proposals received
   - Active contracts
   - Talent reached
   - Hiring success rate

3. **Visualizations**:
   - Charts and graphs
   - Trend analysis
   - Performance metrics

### 12. Settings Flow

**Settings Menu**

1. **Account Settings**:
   - Profile information
   - Email preferences
   - Password change

2. **Appearance**:
   - Theme selection (Light/Dark/System)
   - Color customization

3. **Notifications**:
   - Push notification preferences
   - Email notifications
   - Alert settings

4. **Privacy**:
   - Data management
   - Privacy settings

5. **About**:
   - App version
   - Terms of service
   - Privacy policy

6. **Logout**:
   - Sign out confirmation
   - Session termination

### 13. Drawer Navigation

**Profile Drawer** (Left side)

1. **User Information**:
   - Profile picture
   - Name
   - Email
   - Role badge

2. **Quick Actions**:
   - My Stats
   - Profile
   - Settings
   - Theme

3. **Navigation**:
   - Swipe from left edge to open
   - Tap outside to close
   - Smooth animation

## Core Features

### Authentication and Authorization

- Secure JWT-based authentication
- Role-based access control (Talent/Recruiter)
- Session persistence with token refresh
- Password recovery with email verification
- Remember me functionality

### Mission Management

- Create, edit, and delete missions (Recruiters)
- Browse missions with search and filters (Talent)
- AI-powered mission matching
- Favorite missions
- Real-time proposal counts

### Proposal System

- Submit proposals with cover letters
- AI-powered proposal ranking
- Status tracking (Not Viewed, Viewed, Accepted, Refused)
- Archive and delete functionality
- Swipe actions for quick management

### Contract Management

- Digital contract creation
- Electronic signature capture
- PDF generation and storage
- Contract status tracking
- Secure contract sharing via messaging

### Messaging System

- Real-time conversations
- Contract sharing in messages
- Read/unread indicators
- Message timestamps
- Profile integration

### Portfolio Showcase

- Project gallery with media support
- Image, video, and PDF uploads
- Project descriptions and links
- Grid and list view modes
- Easy project management

### AI Features

- Mission-talent matching algorithm
- Proposal ranking and scoring
- Profile analysis and insights
- Skill gap identification
- Personalized recommendations

### Theme Customization

- Light and dark mode support
- System theme following
- Custom color schemes
- Persistent theme preferences

## Technical Stack

### Frameworks and Libraries

- **Jetpack Compose**: Modern declarative UI framework
- **Kotlin**: Primary programming language
- **Coroutines**: Asynchronous programming
- **Flow**: Reactive data streams
- **Hilt**: Dependency injection
- **Room**: Local database (if needed)
- **Retrofit**: HTTP client for API calls
- **Coil**: Image loading and caching
- **Navigation Component**: Screen navigation

### Architecture Patterns

- **MVVM**: Model-View-ViewModel architecture
- **Clean Architecture**: Separation of concerns
- **Repository Pattern**: Data access abstraction
- **Use Cases**: Business logic encapsulation
- **State Management**: StateFlow and MutableState

### Networking

- **Retrofit**: REST API client
- **OkHttp**: HTTP client with interceptors
- **Gson**: JSON serialization/deserialization
- **SSE (Server-Sent Events)**: Real-time updates

### Storage

- **SharedPreferences**: Simple key-value storage
- **EncryptedSharedPreferences**: Secure credential storage
- **File System**: Media and document storage

## Setup and Installation

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17 or later
- Android SDK 24+ (minimum)
- Android SDK 34 (target)
- Gradle 8.0+

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone [repository-url]
   cd MatchiFy
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Wait for Gradle sync to complete

3. **Configure SDK**
   - Ensure Android SDK is properly installed
   - Install required SDK versions if prompted
   - Sync project with Gradle files

4. **Build and Run**
   - Select a device or emulator
   - Click the Run button or press Shift+F10
   - Wait for build to complete

### Dependencies

All dependencies are managed through Gradle. The project uses:
- Jetpack Compose BOM for UI components
- Retrofit for networking
- Hilt for dependency injection
- Coil for image loading
- Material3 for design components



### Environment Setup

1. **Development**: Uses development API endpoints
2. **Staging**: Uses staging API endpoints (if configured)
3. **Production**: Uses production API endpoints

### Build Configurations

- **Debug**: Development build with logging enabled
- **Release**: Production build with optimizations and ProGuard

## User Roles

### Talent

**Capabilities**:
- Browse and search missions
- View AI-recommended matches
- Submit proposals to missions
- Manage portfolio projects
- Upload CV and documents
- Review and sign contracts
- Message with recruiters
- Track proposal status
- View statistics and insights

**Restrictions**:
- Cannot create missions
- Cannot view other talents' proposals
- Cannot access recruiter-specific features

### Recruiter

**Capabilities**:
- Create and manage missions
- Review received proposals
- Use AI-powered proposal ranking
- Create and send contracts
- Message with talents
- View talent profiles and portfolios
- Track mission performance
- Access hiring statistics

**Restrictions**:
- Cannot submit proposals
- Cannot create portfolio
- Cannot access talent-specific features


## Security

- **Secure Storage**: EncryptedSharedPreferences for sensitive data
- **HTTPS Only**: Encrypted network communication
- **Token Management**: Automatic token refresh
- **Input Validation**: Client-side and server-side validation
- **Session Management**: Secure session handling

## Performance Optimization

- **Lazy Loading**: On-demand data fetching
- **Image Caching**: Coil with automatic caching
- **Pagination**: Large list optimization with Paging 3
- **Background Tasks**: Coroutines for smooth UI
- **Memory Management**: Proper lifecycle handling


## Support

For issues, questions, or contributions, please contact the development team or create an issue in the project repository.

