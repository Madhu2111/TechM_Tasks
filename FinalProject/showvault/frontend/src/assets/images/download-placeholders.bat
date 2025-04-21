@echo off
echo Downloading placeholder images for ShowVault...

curl -o placeholder-movie.jpg https://assets-in.bmscdn.com/iedb/movies/images/mobile/thumbnail/xlarge/generic-movie-image-1.jpg
curl -o placeholder-concert.jpg https://assets-in.bmscdn.com/iedb/events/images/mobile/thumbnail/xlarge/generic-concert-image-1.jpg
curl -o placeholder-theater.jpg https://assets-in.bmscdn.com/iedb/events/images/mobile/thumbnail/xlarge/generic-theater-image-1.jpg
curl -o placeholder-event.jpg https://assets-in.bmscdn.com/iedb/events/images/mobile/thumbnail/xlarge/generic-event-image-1.jpg
curl -o placeholder-venue.jpg https://assets-in.bmscdn.com/venueimages/generic-cinema-image.jpg

echo Placeholder images downloaded successfully!
pause