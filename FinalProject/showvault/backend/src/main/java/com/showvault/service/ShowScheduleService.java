package com.showvault.service;

import com.showvault.model.ShowSchedule;
import com.showvault.repository.ShowScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ShowScheduleService {

    private final ShowScheduleRepository showScheduleRepository;

    @Autowired
    public ShowScheduleService(ShowScheduleRepository showScheduleRepository) {
        this.showScheduleRepository = showScheduleRepository;
    }

    public List<ShowSchedule> getAllShowSchedules() {
        return showScheduleRepository.findAll();
    }

    public Optional<ShowSchedule> getShowScheduleById(Long id) {
        return showScheduleRepository.findById(id);
    }

    public List<ShowSchedule> getShowSchedulesByShowId(Long showId) {
        return showScheduleRepository.findByShowId(showId);
    }

    public List<ShowSchedule> getShowSchedulesByVenueId(Long venueId) {
        return showScheduleRepository.findByVenueId(venueId);
    }

    public List<ShowSchedule> getShowSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        return showScheduleRepository.findByShowDateBetween(startDate, endDate);
    }

    public List<ShowSchedule> getUpcomingSchedulesByShowId(Long showId, LocalDate currentDate) {
        return showScheduleRepository.findUpcomingSchedulesByShowId(showId, currentDate);
    }

    public List<ShowSchedule> getUpcomingSchedulesByCity(String city, LocalDate currentDate) {
        return showScheduleRepository.findUpcomingSchedulesByCity(city, currentDate);
    }

    public List<ShowSchedule> getUpcomingSchedulesByGenre(String genre, LocalDate currentDate) {
        return showScheduleRepository.findUpcomingSchedulesByGenre(genre, currentDate);
    }

    @Transactional
    public ShowSchedule createShowSchedule(ShowSchedule showSchedule) {
        return showScheduleRepository.save(showSchedule);
    }

    @Transactional
    public ShowSchedule updateShowSchedule(ShowSchedule showSchedule) {
        return showScheduleRepository.save(showSchedule);
    }

    @Transactional
    public void deleteShowSchedule(Long id) {
        showScheduleRepository.deleteById(id);
    }

    @Transactional
    public boolean updateShowScheduleStatus(Long scheduleId, ShowSchedule.ScheduleStatus newStatus) {
        Optional<ShowSchedule> scheduleOpt = showScheduleRepository.findById(scheduleId);
        
        if (scheduleOpt.isPresent()) {
            ShowSchedule schedule = scheduleOpt.get();
            schedule.setStatus(newStatus);
            showScheduleRepository.save(schedule);
            return true;
        }
        
        return false;
    }

    public List<ShowSchedule> searchShowSchedules(Long showId, Long venueId, LocalDate fromDate, LocalDate toDate, ShowSchedule.ScheduleStatus status) {
        return showScheduleRepository.findByShowIdAndVenueIdAndShowDateBetweenAndStatus(
            showId,
            venueId,
            fromDate,
            toDate,
            status
        );
    }

    public List<ShowSchedule> getShowSchedulesByStatus(ShowSchedule.ScheduleStatus status) {
        return showScheduleRepository.findByStatus(status);
    }

    public List<ShowSchedule> getShowSchedulesByDate(LocalDate date) {
        return showScheduleRepository.findByShowDate(date);
    }

    public List<ShowSchedule> getUpcomingShowSchedules() {
        LocalDate currentDate = LocalDate.now();
        return showScheduleRepository.findByShowDateGreaterThanEqual(currentDate);
    }
}