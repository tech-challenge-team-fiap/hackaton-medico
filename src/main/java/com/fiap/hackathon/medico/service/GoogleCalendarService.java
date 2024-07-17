package com.fiap.hackathon.medico.service;

import com.fiap.hackathon.medico.domain.entity.ScheduleDB;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "Hackathon Fiap Grupo 5";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = List.of(CalendarScopes.CALENDAR_EVENTS, CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials/google/credential.json";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
    }

    public void creatingNewEvent(ScheduleDB schedule) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service =
                new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        Event event = new Event();
        event.setSummary("Appointment withing " + schedule.getPatientName());

        ZonedDateTime zdt = schedule.getScheduleTime().atZone(ZoneId.of("America/Sao_Paulo"));

        Date start = new Date(zdt.toInstant().toEpochMilli());

        DateTime startDate = dateTime(start);
        DateTime endDate = dateTime(new Date(start.getTime() + getMiles()));

        event.setStart(new EventDateTime().setDateTime(startDate));
        event.setEnd(new EventDateTime().setDateTime(endDate));
        event.setAttendees(Collections.singletonList(guest(schedule.getPatientEmail())));

        service.events().insert("thales.alexandre.jolo@gmail.com", event).execute();
    }

    private EventAttendee guest(String email) {
        EventAttendee guest = new EventAttendee();
        guest.setEmail(email);

        return guest;
    }

    private Long getMiles() {
        return (long) (50*60000);
    }

    private DateTime dateTime(Date date) {
        return new DateTime(date, TimeZone.getTimeZone("BET"));
    }
}
