package ru.practicum.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.constants.Constants;
import ru.practicum.dto.compilation.CompilationDtoResponse;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventParamAdmin;
import ru.practicum.dto.event.EventParamPublic;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventDto;
import ru.practicum.dto.mapper.CategoryMapper;
import ru.practicum.dto.mapper.CompilationMapper;
import ru.practicum.dto.mapper.EventMapper;
import ru.practicum.dto.mapper.RequestMapper;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestStatusUpdateDto;
import ru.practicum.dto.request.RequestStatusUpdateResult;
import ru.practicum.enums.State;
import ru.practicum.enums.StateAction;
import ru.practicum.enums.Status;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.DataViolationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Compilation;
import ru.practicum.model.CompilationEvent;
import ru.practicum.model.Event;
import ru.practicum.model.QEvent;
import ru.practicum.model.Request;
import ru.practicum.repository.CompilationEventRepository;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.utils.PaginationServiceClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CompilationRepository compilationRepository;
    private final CompilationEventRepository compilationEventRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final CategoryMapper categoryMapper;
    private final CompilationMapper compilationMapper;
    private final UserService userService;
    private final CategoryService categoryService;

    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto eventDto) {
        var user = userService.findUserById(userId);
        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DataViolationException("Дата и время, на которые намечено событие, не может быть раньше, чем через два часа от текущего момента.");
        } else {
            var event = eventMapper.toEventFromNewEventDto(eventDto);
            var category = categoryMapper.toCategory(categoryService.findCategoryById(eventDto.getCategory()));
            event.setCategory(category);
            event.setInitiator(user);
            event.setState(State.PENDING);
            event.setLat(eventDto.getLocation().getLat());
            event.setLon(eventDto.getLocation().getLon());

            if (eventDto.getRequestModeration() == null) {
                event.setRequestModeration(true);
            }
            if (eventDto.getParticipantLimit() == null) {
                event.setParticipantLimit(0);
            }
            if (eventDto.getPaid() == null) {
                event.setPaid(false);
            }
            event.setConfirmedRequests(0);
            event.setViews(0);
            return eventMapper.toEventFullDto(eventRepository.save(event));
        }
    }

    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDto eventDto) {
        if (eventDto.getEventDate() != null) {
            if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Дата и время, на которые намечено событие, не может быть раньше, чем через два часа от текущего момента.");
            }
        }
        if (eventDto.getParticipantLimit() != null && eventDto.getParticipantLimit() < 0) {
            throw new BadRequestException("Нельзя задать отрицательное значение на лимит пользователей:" + eventDto.getParticipantLimit());
        }
        if (eventDto.getAnnotation() != null) {
            if (eventDto.getAnnotation().length() < 20 || eventDto.getAnnotation().length() > 2000) {
                throw new BadRequestException("Краткое описание события не соответствует условию длины (min=20,max=2000) length =" + eventDto.getAnnotation().length());
            }
        }
        if (eventDto.getDescription() != null) {
            if (eventDto.getDescription().length() < 20 || eventDto.getDescription().length() > 7000) {
                throw new BadRequestException("Полное описание события не соответствует условию длины (min=20,max=7000) length =" + eventDto.getDescription().length());
            }
        }
        if (eventDto.getTitle() != null) {
            if (eventDto.getTitle().length() < 3 || eventDto.getTitle().length() > 120) {
                throw new BadRequestException("Заголовок события не соответствует условию длины (min=3,max=120) length =" + eventDto.getTitle().length());
            }
        }

        var event = eventMapper.toEventFromFullEventDto(findUserEventById(userId, eventId));
        if (event.getState().equals(State.PUBLISHED)) {
            throw new DataViolationException("Нельзя менять события со статусом - " + event.getState());
        }

        Category category;
        if (eventDto.getCategory() != null) {
            category = categoryMapper.toCategory(categoryService.findCategoryById(eventDto.getCategory()));
        } else {
            category = categoryMapper.toCategory(categoryService.findCategoryById(event.getCategory().getId()));
        }
        event.setCategory(category);
        eventMapper.updateEventFromUpdateEventDto(eventDto, event);
        if (eventDto.getStateAction() != null && eventDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            event.setState(State.CANCELED);
        }
        if (eventDto.getStateAction() != null && eventDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            event.setState(State.PENDING);
        }
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    public EventFullDto findUserEventById(Long userId, Long eventId) {
        userService.findUserById(userId);
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id = " + eventId + " не найдено.")
        );
        if (event.getInitiator().getId().equals(userId)) {
            return eventMapper.toEventFullDto(event);
        } else {
            throw new BadRequestException("Пользователь с id - " + userId + " не добавлял событие с id - " + eventId);
        }
    }

    public List<EventShortDto> findUserEvents(Long userId, Integer from, Integer size) {
        userService.findUserById(userId);
        var page = PaginationServiceClass.pagination(from, size);
        var events = eventRepository.findByInitiatorIdOrderByCreatedOnDesc(userId, page);
        return !events.isEmpty() ? events.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList()) : Collections.emptyList();
    }

    public List<RequestDto> findEventRequestsById(Long userId, Long eventId) {
        findUserEventById(userId, eventId);
        var request = requestRepository.findByEventId(eventId);
        return request.stream().map(requestMapper::toRequestDto).toList();
    }

    @Transactional
    public RequestDto addRequest(Long userId, Long eventId) {
        var user = userService.findUserById(userId);
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id = " + eventId + " не найдено.")
        );
        var request = new Request();

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new DataViolationException("Запрос уже существует с eventId = " + eventId + " и userId = " + userId);
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new DataViolationException("Инициатор события с id = " + userId + " не может добавить запрос на участие в своём событии c id = " + eventId);
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new DataViolationException("Нельзя участвовать в неопубликованном событии.");
        }
        if (event.getRequestModeration()) {
            if (event.getParticipantLimit() == 0) {
                request.setStatus(Status.CONFIRMED);
                if (event.getConfirmedRequests() != 0) {
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                } else {
                    event.setConfirmedRequests(1);
                }
            } else {
                request.setStatus(Status.PENDING);
            }
            eventRepository.save(event);
            request.setRequester(user);
            request.setEvent(event);

        } else {
            if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= event.getConfirmedRequests()) {
                throw new DataViolationException("У события достигнут лимит запросов на участие.");
            }
            if (event.getConfirmedRequests() != 0) {
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            } else {
                event.setConfirmedRequests(1);
            }
            eventRepository.save(event);
            request.setRequester(user);
            request.setEvent(event);
            request.setStatus(Status.CONFIRMED);
        }
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        userService.findUserById(userId);
        var request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос с id = " + requestId + " не найден.")
        );
        if (request.getRequester().getId().equals(userId) && !request.getStatus().equals(Status.REJECTED)) {
            request.setStatus(Status.CANCELED);
            return requestMapper.toRequestDto(requestRepository.save(request));
        } else {
            throw new DataViolationException("Такого не было в ТЗ, сам написал исключение, наблюдаем))"); // проверка ннннадо?
        }
    }

    public List<RequestDto> getAllUserRequests(Long userId) {
        userService.findUserById(userId);
        var requests = requestRepository.findByRequesterId(userId);
        return !requests.isEmpty() ? requests.stream().map(requestMapper::toRequestDto).collect(Collectors.toList()) : Collections.emptyList();
    }

    public List<EventFullDto> getAllEventsWithParam(EventParamAdmin eventParamAdmin) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        BooleanExpression byUsers = null;
        BooleanExpression byStates = null;
        BooleanExpression byCategories = null;
        BooleanExpression byDateTime = null;

        if (eventParamAdmin.getRangeStart() != null) {
            start = LocalDateTime.parse(eventParamAdmin.getRangeStart(), DateTimeFormatter.ofPattern(Constants.DATE_PATTERN));
        }
        if (eventParamAdmin.getRangeEnd() != null) {
            end = LocalDateTime.parse(eventParamAdmin.getRangeEnd(), DateTimeFormatter.ofPattern(Constants.DATE_PATTERN));
        }
        if (eventParamAdmin.getUsers() != null && !eventParamAdmin.getUsers().isEmpty()) {
            byUsers = QEvent.event.initiator.id.in(eventParamAdmin.getUsers());
        }
        if (eventParamAdmin.getStates() != null && !eventParamAdmin.getStates().isEmpty()) {
            byStates = QEvent.event.state.in(eventParamAdmin.getStates());
        }
        if (eventParamAdmin.getCategories() != null && !eventParamAdmin.getCategories().isEmpty()) {
            byCategories = QEvent.event.category.id.in(eventParamAdmin.getCategories());
        }
        if (start != null && end != null) {
            byDateTime = QEvent.event.eventDate.between(start, end);
        }
        var page = PaginationServiceClass.pagination(eventParamAdmin.getFrom(), eventParamAdmin.getSize());

        Page<Event> events = null;
        if (byDateTime != null) {
            events = eventRepository.findAll(byDateTime.and(byUsers).and(byStates).and(byCategories), page);
        }
        if(byDateTime == null && byCategories==null && byUsers==null && byStates == null){
            events = eventRepository.findAll(page);
        }
        return events != null ? events.stream().map(eventMapper::toEventFullDto).collect(Collectors.toList()) : Collections.emptyList();
    }

    @Transactional
    public EventFullDto eventManager(Long eventId, UpdateEventDto eventDto) {
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Запрос с id = " + eventId + " не найден."));

        if (eventDto.getEventDate() != null && eventDto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата начала - " + eventDto.getEventDate() +
                    " изменяемого события c id = " + eventId +
                    " не должна быть в прошлом"); //eventDto.getEventDate().isBefore(event.getEventDate()) &&
        }
        if (eventDto.getAnnotation() != null) {
            if (eventDto.getAnnotation().length() < 20 || eventDto.getAnnotation().length() > 2000) {
                throw new BadRequestException("Краткое описание события не соответствует условию длины (min=20,max=2000) " + eventDto.getAnnotation().length());
            }
        }
        if (eventDto.getDescription() != null) {
            if (eventDto.getDescription().length() < 20 || eventDto.getDescription().length() > 7000) {
                throw new BadRequestException("Полное описание события не соответствует условию длины (min=20,max=7000) " + eventDto.getDescription().length());
            }
        }
        if (eventDto.getTitle() != null) {
            if (eventDto.getTitle().length() < 3 || eventDto.getTitle().length() > 120) {
                throw new BadRequestException("Заголовок события не соответствует условию длины (min=3,max=120) " + eventDto.getTitle().length());
            }
        }
        if (eventDto.getStateAction() != null) {
            if (event.getState().equals(State.PUBLISHED) || event.getState().equals(State.CANCELED)) {
                throw new DataViolationException("Cобытие можно публиковать, только если оно в состоянии ожидания публикации");
            }
            if (event.getState().equals(State.PENDING) && eventDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                eventMapper.updateEventFromUpdateEventDto(eventDto, event);
            }
            if (event.getState().equals(State.PENDING) && eventDto.getStateAction().equals(StateAction.REJECT_EVENT)) {
                event.setState(State.CANCELED);
                event.setPublishedOn(LocalDateTime.now());
                eventMapper.updateEventFromUpdateEventDto(eventDto, event);
            }
        }
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Transactional
    public RequestStatusUpdateResult requestManager(Long userId, Long eventId, RequestStatusUpdateDto requestStatusUpdateDto) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new BadRequestException("Пользователь с id = " + userId + " ,не создавал событие с id = " + eventId + ".");
        }
        var user = userService.findUserById(userId);
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id = " + eventId + " не найдено.")
        );
        var requests = requestRepository.findAllById(requestStatusUpdateDto.getRequestIds());
        if (requestStatusUpdateDto.getStatus().equals(Status.CONFIRMED)) {
            for (Request request : requests) {
                if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= event.getConfirmedRequests()) {
                    throw new DataViolationException("У события достигнут лимит запросов на участие.");
                }
                request.setStatus(Status.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            }
        } else if (requestStatusUpdateDto.getStatus().equals(Status.REJECTED)) {
            for (Request request : requests) {
                if (request.getStatus().equals(Status.CONFIRMED)) {
                    throw new DataViolationException("Нельзя отменять уже одобренную заявку с id = " + request.getId());
                }
                request.setStatus(Status.REJECTED);
            }
        }
        requestRepository.saveAll(requests);

        var requestsResult = requestRepository.findByEventId(eventId);
        var requestMap = requestsResult.stream().collect(Collectors.groupingBy(Request::getStatus));
        List<RequestDto> rejectedRequests = new ArrayList<>();
        if (requestMap.get(Status.REJECTED) != null) {
            rejectedRequests = requestMap.get(Status.REJECTED).stream().map(requestMapper::toRequestDto).toList();
        }
        List<RequestDto> confirmedRequests = new ArrayList<>();
        if (requestMap.get(Status.CONFIRMED) != null) {
            confirmedRequests = requestMap.get(Status.CONFIRMED).stream().map(requestMapper::toRequestDto).toList();
        }
        RequestStatusUpdateResult result = new RequestStatusUpdateResult();
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);
        return result;
    }

    public List<EventShortDto> getEvents(EventParamPublic eventParamPublic) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        BooleanExpression byText = null;
        BooleanExpression byCategories = null;
        BooleanExpression byPaid = null;
        BooleanExpression byDateTime = null;
        BooleanExpression byStates = QEvent.event.state.in(State.PUBLISHED);

        if (eventParamPublic.getRangeStart() != null) {
            start = LocalDateTime.parse(eventParamPublic.getRangeStart(), DateTimeFormatter.ofPattern(Constants.DATE_PATTERN));
        }
        if (eventParamPublic.getRangeEnd() != null) {
            end = LocalDateTime.parse(eventParamPublic.getRangeEnd(), DateTimeFormatter.ofPattern(Constants.DATE_PATTERN));
            if (end.isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Дата окончания не может быть раньше текущей даты");
            }
        }
        if (eventParamPublic.getText() != null && !eventParamPublic.getText().isBlank()) {
            byText = QEvent.event.annotation.toLowerCase().likeIgnoreCase("%" + eventParamPublic.getText().toLowerCase() + "%")
                    .or(QEvent.event.description.toLowerCase().likeIgnoreCase("%" + eventParamPublic.getText().toLowerCase() + "%"));
        }
        if (eventParamPublic.getPaid() != null) {
            byPaid = QEvent.event.paid.in(eventParamPublic.getPaid());
        }
        if (eventParamPublic.getCategories() != null && !eventParamPublic.getCategories().isEmpty()) {
            byCategories = QEvent.event.category.id.in(eventParamPublic.getCategories());
        }
        if (start != null && end != null) {
            byDateTime = QEvent.event.eventDate.between(start, end);
        } else {
            byDateTime = QEvent.event.eventDate.after(LocalDateTime.now());
        }
        var page = PaginationServiceClass.pagination(eventParamPublic.getFrom(), eventParamPublic.getSize());

        Page<Event> events = null;
        if (byDateTime != null) {
            events = eventRepository.findAll(byDateTime.and(byText).and(byStates).and(byPaid).and(byCategories), page);
        }
        if (events != null) {
            if (eventParamPublic.getOnlyAvailable() != null && eventParamPublic.getOnlyAvailable()) {
                var availableEvents = events.stream().filter(event -> event.getParticipantLimit() < event.getConfirmedRequests()).map(eventMapper::toEventShortDto).toList();
                if (eventParamPublic.getSort().equals("EVENT_DATE")) {
                    return availableEvents.stream().sorted(Comparator.comparing(EventShortDto::getEventDate)).toList();
                }
                if (eventParamPublic.getSort().equals("VIEWS")) {
                    return availableEvents.stream().sorted(Comparator.comparing(EventShortDto::getViews)).toList();
                }
            } else {
                if (eventParamPublic.getSort() != null && eventParamPublic.getSort().equals("EVENT_DATE")) {
                    return events.stream().map(eventMapper::toEventShortDto).sorted(Comparator.comparing(EventShortDto::getEventDate)).toList();
                }
                if (eventParamPublic.getSort() != null && eventParamPublic.getSort().equals("VIEWS")) {
                    return events.stream().map(eventMapper::toEventShortDto).sorted(Comparator.comparing(EventShortDto::getViews)).toList();
                }
            }
            return events.stream().map(eventMapper::toEventShortDto).toList();
        }
        return Collections.emptyList();
        //статистика
    }

    public EventFullDto findEventById(Long eventId) {
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id = " + eventId + " не найдено.")
        );
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие с id = " + eventId + " еще не опубликовано.");
        }
        return eventMapper.toEventFullDto(event);
        //статистика не реализована
    }

    @Transactional
    public CompilationDtoResponse addCompilation(NewCompilationDto compilationDto) {
        var compilation = compilationMapper.toCompilation(compilationDto);
        compilation.setPinned(Boolean.TRUE.equals(compilationDto.getPinned()));
        var createdComp = compilationRepository.save(compilation);
        if (compilationDto.getEvents() != null && !compilationDto.getEvents().isEmpty()) {
            return saveEventsCompilation(compilationDto.getEvents(), createdComp);
        }
        var result = compilationMapper.toCompilationDtoResponse(createdComp);
        result.setEvents(Collections.emptyList());
        return result;
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        var comp = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Компиляция с id = " + compId + "не найдена"));
        compilationRepository.deleteById(compId);
        compilationEventRepository.deleteAllByCompilation_Id(compId);
    }

    @Transactional
    public CompilationDtoResponse updateCompilation(Long compId, NewCompilationDto compilationDto) {
        var comp = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Компиляция с id = " + compId + "не найдена"));
        if (compilationDto.getTitle() != null && compilationDto.getTitle().length() < 1 || compilationDto.getTitle() != null && compilationDto.getTitle().length() > 50) {
            throw new BadRequestException("Длина title не соответствует параметрам (min = 1, max = 50)");
        }
        compilationMapper.updateCompilationFromUpdateCompilationDto(compilationDto, comp);
        compilationRepository.save(comp);

        if (compilationDto.getEvents() != null && !compilationDto.getEvents().isEmpty()) {
            compilationEventRepository.deleteAllByCompilation_Id(compId);
            return saveEventsCompilation(compilationDto.getEvents(), comp);
        }
        compilationEventRepository.deleteAllByCompilation_Id(compId);
        var result = compilationMapper.toCompilationDtoResponse(comp);
        result.setEvents(Collections.emptyList());
        return result;
    }

    public CompilationDtoResponse findCompilationById(Long compId) {
        var comp = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Компиляция с id = " + compId + "не найдена"));

        var compEvents = compilationEventRepository.findAllByCompilation_Id(comp.getId());
        var eventShortDto = compEvents.stream().map(CompilationEvent::getEvent).map(eventMapper::toEventShortDto).toList();
        var result = compilationMapper.toCompilationDtoResponse(comp);
        result.setEvents(eventShortDto);
        return result;
    }

    public List<CompilationDtoResponse> findCompilationsByParam(Boolean pinned, Integer from, Integer size) {
        var page = PaginationServiceClass.pagination(from, size);
        var compilations = compilationRepository.findByPinned(pinned, page);
        var compIds = compilations.stream().map(Compilation::getId).toList();
        var compEvents = compilationEventRepository.findByCompilation_IdIn(compIds);
        var compEventMap = compEvents.stream().collect(Collectors.groupingBy(CompilationEvent::getCompilation));

        List<CompilationDtoResponse> result = new ArrayList<>();
        for (Compilation compilation : compilations) {
            var compEventsList = compEventMap.get(compilation);
            var compilationDtoResponse = compilationMapper.toCompilationDtoResponse(compilation);
            if (compEventsList != null && !compEventsList.isEmpty()) {
                var eventShortDto = compEventsList.stream().map(CompilationEvent::getEvent).map(eventMapper::toEventShortDto).toList();
                compilationDtoResponse.setEvents(eventShortDto);
            } else {
                compilationDtoResponse.setEvents(Collections.emptyList());
            }
            result.add(compilationDtoResponse);
        }
        return result;
    }

    private CompilationDtoResponse saveEventsCompilation(Set<Long> eventIds, Compilation compilation) {
        var events = eventRepository.findAllById(eventIds);
        List<CompilationEvent> compilationEvents = new ArrayList<>();
        for (Event event : events) {
            CompilationEvent compilationEvent = new CompilationEvent();
            compilationEvent.setCompilation(compilation);
            compilationEvent.setEvent(event);
            compilationEventRepository.save(compilationEvent);
            compilationEvents.add(compilationEvent);
        }
        var eventShortDto = compilationEvents.stream().map(CompilationEvent::getEvent).map(eventMapper::toEventShortDto).toList();
        var result = compilationMapper.toCompilationDtoResponse(compilation);
        result.setEvents(eventShortDto);
        return result;
    }
}
