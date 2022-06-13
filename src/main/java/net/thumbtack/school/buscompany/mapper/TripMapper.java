package net.thumbtack.school.buscompany.mapper;

import net.thumbtack.school.buscompany.dto.request.ScheduleDtoRequest;
import net.thumbtack.school.buscompany.dto.request.TripDtoRequest;
import net.thumbtack.school.buscompany.dto.response.TripDtoResponse;
import net.thumbtack.school.buscompany.error.BusAppErrorCode;
import net.thumbtack.school.buscompany.error.BusAppException;
import net.thumbtack.school.buscompany.model.*;

import java.sql.Time;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TripMapper {

    public static final TripMapper INSTANCE = new TripMapper();

    private TripMapper() {}

    public Trip dtoToTrip(TripDtoRequest dto, Bus bus) throws BusAppException {
        Trip trip = new Trip();
        trip.setBus(bus);
        trip.setFromStation(dto.getFromStation());
        trip.setToStation(dto.getToStation());
        trip.setStart(Time.valueOf(dto.getStart() + ":00"));
        trip.setPrice(dto.getPrice());
        trip.setDuration(MapperUtils.minutesFromTimeStr(dto.getDuration()));
        List<TripDate> dates = new ArrayList<>();
        if (dto.getSchedule() != null) {
            TripSchedule tripSchedule = TripScheduleMapper.INSTANCE.dtoToTripSchedule(dto.getSchedule());
            trip.setSchedule(tripSchedule);
            addDatesToListFromSchedule(dates, dto.getSchedule());
        } else if (dto.getDates() != null) {
            dto.getDates().forEach(date -> addDateToList(dates, LocalDate.parse(date)));
        }
        if (dates.isEmpty()) {
            throw new BusAppException(BusAppErrorCode.EMPTY_TRIP_DATES);
        }
        trip.setDates(dates);
        trip.setApproved(false);
        return trip;
    }

    public TripDtoResponse tripToDto(Trip trip) {
        TripDtoResponse dto = new TripDtoResponse();
        dto.setId(trip.getId());
        dto.setFromStation(trip.getFromStation());
        dto.setToStation(trip.getToStation());
        dto.setStart(MapperUtils.strFromTime(trip.getStart()));
        dto.setDuration(MapperUtils.timeStrFromMinutes(trip.getDuration()));
        dto.setPrice(trip.getPrice());
        dto.setBus(BusMapper.INSTANCE.busToDto(trip.getBus()));
        dto.setApproved(trip.isApproved());
        dto.setSchedule(TripScheduleMapper.INSTANCE.tripScheduleToDto(trip.getSchedule()));
        dto.setDates(trip.getDates().stream().map(TripDate::getDate).map(LocalDate::toString).collect(Collectors.toList()));
        return dto;
    }

    private void addDatesToListFromSchedule(List<TripDate> dates, ScheduleDtoRequest schedule) {
        LocalDate date = LocalDate.parse(schedule.getFromDate());
        SchedulePeriod schedulePeriod = new SchedulePeriod(schedule.getPeriod());
        do {
            if (schedulePeriod.getDaysType() != null) {
                switch (schedulePeriod.getDaysType()) {
                    case "daily":
                        addDateToList(dates, date);
                        break;
                    case "odd":
                        if (date.getDayOfMonth() % 2 != 0) {
                            addDateToList(dates, date);
                        }
                        break;
                    case "even":
                        if (date.getDayOfMonth() % 2 == 0) {
                            addDateToList(dates, date);
                        }
                        break;
                }
            }
            if (schedulePeriod.getDaysOfMonth() != null && List.of(schedulePeriod.getDaysOfMonth()).contains(date.getDayOfMonth())) {
                addDateToList(dates, date);
            }
            if (schedulePeriod.getDaysOfWeek() != null && List.of(schedulePeriod.getDaysOfWeek()).contains(date.getDayOfWeek())) {
                addDateToList(dates, date);
            }
            date = date.plusDays(1);
        } while (!date.isAfter(LocalDate.parse(schedule.getToDate())));
    }

    private void addDateToList(List<TripDate> dates, LocalDate date) {
        TripDate tripDate = new TripDate();
        tripDate.setDate(date);
        dates.add(tripDate);
    }

}
