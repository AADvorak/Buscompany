package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.ApplicationProperties;
import net.thumbtack.school.buscompany.dto.request.TripDtoRequest;
import net.thumbtack.school.buscompany.dto.response.BusDtoResponse;
import net.thumbtack.school.buscompany.dto.response.TripDtoResponse;
import net.thumbtack.school.buscompany.error.BusAppErrorCode;
import net.thumbtack.school.buscompany.error.BusAppException;
import net.thumbtack.school.buscompany.mapper.BusMapper;
import net.thumbtack.school.buscompany.mapper.TripMapper;
import net.thumbtack.school.buscompany.model.*;
import net.thumbtack.school.buscompany.repository.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService extends ServiceBase {

    private final BusRepository busRepository;

    private final TripRepository tripRepository;

    private final TripDateRepository tripDateRepository;

    private final PlaceRepository placeRepository;

    public TripService(UserSessionRepository userSessionRepository, BusRepository busRepository,
                       TripRepository tripRepository, TripDateRepository tripDateRepository,
                       ApplicationProperties applicationProperties, PlaceRepository placeRepository) {
        super(userSessionRepository, applicationProperties);
        this.busRepository = busRepository;
        this.tripRepository = tripRepository;
        this.tripDateRepository = tripDateRepository;
        this.placeRepository = placeRepository;
    }

    public List<BusDtoResponse> getAllBuses(String sessionId) throws BusAppException {
        if (!(getUserBySessionId(sessionId) instanceof Admin)) {
            throw new BusAppException(BusAppErrorCode.NO_PERMISSION);
        }
        return busRepository.findAll().stream().map(BusMapper.INSTANCE::busToDto).collect(Collectors.toList());
    }

    public TripDtoResponse insertTrip(String sessionId, TripDtoRequest request) throws BusAppException {
        if (!(getUserBySessionId(sessionId) instanceof Admin)) {
            throw new BusAppException(BusAppErrorCode.NO_PERMISSION);
        }
        Bus bus = busRepository.findById(request.getBusId()).orElseThrow(() -> new BusAppException(BusAppErrorCode.BUS_NOT_FOUND));
        return TripMapper.INSTANCE.tripToDto(tripRepository.save(TripMapper.INSTANCE.dtoToTrip(request, bus)));
    }

    @Transactional
    public TripDtoResponse updateTrip(String sessionId, TripDtoRequest request, int id) throws BusAppException {
        if (!(getUserBySessionId(sessionId) instanceof Admin)) {
            throw new BusAppException(BusAppErrorCode.NO_PERMISSION);
        }
        Bus bus = busRepository.findById(request.getBusId()).orElseThrow(() -> new BusAppException(BusAppErrorCode.BUS_NOT_FOUND));
        Trip trip = TripMapper.INSTANCE.dtoToTrip(request, bus);
        Trip existingTrip = tripRepository.findById(id).orElseThrow(
                () -> new BusAppException(BusAppErrorCode.TRIP_NOT_FOUND));
        if (existingTrip.isApproved()) {
            throw new BusAppException(BusAppErrorCode.TRIP_IS_NOT_EDITABLE);
        }
        tripDateRepository.deleteAllByTripId(id); // todo убрать и разобраться как обновить Trip 1 запросом
        existingTrip.setBus(trip.getBus());
        existingTrip.setFromStation(trip.getFromStation());
        existingTrip.setToStation(trip.getToStation());
        existingTrip.setStart(trip.getStart());
        existingTrip.setPrice(trip.getPrice());
        existingTrip.setDuration(trip.getDuration());
        existingTrip.setSchedule(trip.getSchedule());
        existingTrip.setDates(trip.getDates());
        return TripMapper.INSTANCE.tripToDto(tripRepository.save(existingTrip));
    }

    public void deleteTrip(String sessionId, int id) throws BusAppException {
        if (!(getUserBySessionId(sessionId) instanceof Admin)) {
            throw new BusAppException(BusAppErrorCode.NO_PERMISSION);
        }
        Trip existingTrip = tripRepository.findById(id).orElseThrow(
                () -> new BusAppException(BusAppErrorCode.TRIP_NOT_FOUND));
        if (existingTrip.isApproved()) {
            throw new BusAppException(BusAppErrorCode.TRIP_IS_NOT_EDITABLE);
        }
        tripRepository.delete(existingTrip);
    }

    public TripDtoResponse getTripInfo(String sessionId, int id) throws BusAppException {
        if (!(getUserBySessionId(sessionId) instanceof Admin)) {
            throw new BusAppException(BusAppErrorCode.NO_PERMISSION);
        }
        return TripMapper.INSTANCE.tripToDto(tripRepository.findById(id).orElseThrow(
                () -> new BusAppException(BusAppErrorCode.TRIP_NOT_FOUND)));
    }

    public TripDtoResponse approveTripAndCreatePlaces(String sessionId, int id) throws BusAppException {
        if (!(getUserBySessionId(sessionId) instanceof Admin)) {
            throw new BusAppException(BusAppErrorCode.NO_PERMISSION);
        }
        Trip existingTrip = tripRepository.findById(id).orElseThrow(
                () -> new BusAppException(BusAppErrorCode.TRIP_NOT_FOUND));
        existingTrip.setApproved(true);
        insertPlacesForTrip(existingTrip);
        return TripMapper.INSTANCE.tripToDto(tripRepository.save(existingTrip));
    }

    public List<TripDtoResponse> findTrips(String sessionId, String fromStation, String toStation, String busName,
                                           LocalDate fromDate, LocalDate toDate) throws BusAppException {
        getUserBySessionId(sessionId);
        Specification<Trip> specification = Specification
                .where(TripSpecifications.isApproved())
                .and(fromDate == null ? null : TripSpecifications.existsDateAfter(fromDate))
                .and(toDate == null ? null : TripSpecifications.existsDateBefore(toDate))
                .and(fromStation == null ? null : TripSpecifications.fromStationLike(fromStation))
                .and(toStation == null ? null : TripSpecifications.toStationLike(toStation))
                .and(busName == null ? null : TripSpecifications.busNameLike(busName));
        return tripRepository.findAll(specification).stream().map(TripMapper.INSTANCE::tripToDto).collect(Collectors.toList());
    }

    private void insertPlacesForTrip(Trip trip) {
        List<Place> places = new ArrayList<>();
        int placeCount = trip.getBus().getPlaceCount();
        for (TripDate tripDate : trip.getDates()) {
            for (int number = 1; number <= placeCount; number++) {
                Place place = new Place();
                place.setNumber(number);
                place.setTripDate(tripDate);
                places.add(place);
            }
        }
        placeRepository.saveAll(places);
    }

}
