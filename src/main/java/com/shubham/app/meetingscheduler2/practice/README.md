# Meeting Room Scheduler


### Actors : 
- Admin
- User

### Functional Requirements

- Admin should be able to do setup and create n meeting rooms
- User should be able to book a vacant meeting for a scheduled interval
- User should be able to cancel a scheduled meeting 


## Out of Scope 
- Video conferencing in a meeting room


## Core Entities 
- Room
- Meeting (start, end)
- RoomStatus Enum (vacant, booked)
- User (id, name)
- RoomsCreationsService
- MeetingRoomAllocationService

## Errors
- RoomAlreadyReserved
- 

 