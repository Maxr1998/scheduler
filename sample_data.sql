INSERT OR
REPLACE INTO Studycourse (id, name, revision)
VALUES (1, 'B.Sc. Informatik', 'PO 2018'),
       (2, 'B.Sc. Ingenieurinformatik', 'PO 2018'),
       (3, 'B.Sc. Medizinische Informatik', '');

INSERT OR
REPLACE INTO Event (id, name, module, duration)
VALUES (1, 'Informatik 2', '', 90),
       (2, 'Übung zu Informatik 2', '', 90),
       (3, 'Einführung in die Theoretische Informatik', '', 90),
       (4, 'Mathematik für Informatiker II', '', 90),
       (5, 'Übung zu Mathematik für Informatiker II', '', 90),
       (6, 'Globalübung - Mathematik für Informatiker II', '', 90),
       (7, 'Systemnahe Informatik', '', 90),
       (8, 'Übung zu Systemnahe Informatik', '', 90),
       (9, 'Grundlagen der Human-Computer Interaction / Multimedia Grundlagen II', '', 90),
       (10, 'Übung zu Grundlagen der Human-Computer Interaction / Multimedia Grundlagen II', '', 90),
       (11, 'Softwareprojekt', '', 90),
       (12, 'Übung zu Softwareprojekt', '', 90),
       (13, 'Ad-Hoc und Sensornetze', '', 90),
       (14, 'Übung zu Ad-Hoc und Sensornetze', '', 90),
       (15, 'Safety and Security', '', 90),
       (16, 'Übung zu Safety and Security', '', 90),
       (17, 'Ingenieurwissenschaftliche Grundlagen', '', 90),
       (18, 'Übung zu Ingenieurwissenschaftliche Grundlagen', '', 90),
       (19, 'Konstruktionslehre', '', 90),
       (20, 'Übung zu Konstruktionslehre', '', 90);


INSERT OR
REPLACE INTO StudycourseEvent (studycourse, event, semester, required)
VALUES
    /* B.Sc. Informatik */
    (1, 1, 2, true),
    (1, 2, 2, true),
    (1, 3, 2, true),
    (1, 4, 2, true),
    (1, 5, 2, true),
    (1, 6, 2, true),
    (1, 7, 4, true),
    (1, 8, 4, true),
    (1, 9, 4, true),
    (1, 10, 4, true),
    (1, 11, 4, true),
    (1, 12, 4, true),
    (1, 13, 4, false),
    (1, 14, 4, false),
    (1, 15, 6, false),
    (1, 16, 6, false),
    /* B.Sc. Ingenieurinformatik */
    (2, 1, 2, true),
    (2, 2, 2, true),
    (2, 4, 2, true),
    (2, 5, 2, true),
    (2, 6, 2, true),
    (2, 17, 2, true),
    (2, 18, 2, true),
    (2, 19, 2, true),
    (2, 20, 2, true),
    (2, 7, 4, true),
    (2, 8, 4, true);

INSERT OR
REPLACE INTO Timeslot (semester, start_time, end_time)
VALUES
    /* SS2021 */
    (20211, 495, 585),
    (20211, 615, 705),
    (20211, 735, 825),
    (20211, 735, 825),
    (20211, 855, 945),
    (20211, 975, 1065),
    (20211, 1095, 1185),
    /* WS2021/22 */
    (20212, 495, 585),
    (20212, 615, 705),
    (20212, 735, 825),
    (20212, 735, 825),
    (20212, 855, 945),
    (20212, 975, 1065),
    (20212, 1095, 1185);

INSERT OR
REPLACE INTO Schedule (semester, event, day, start_time, room)
VALUES
    /* Informatik II Tue & Thu */
    (20211, 1, 2, 615, -1),
    (20211, 1, 4, 615, -1),
    /* TI Mon & Thu */
    (20211, 3, 1, 975, -1),
    (20211, 3, 4, 975, -1),
    /* MFI II Mon & Tue */
    (20211, 4, 1, 615, -1),
    (20211, 4, 2, 855, -1);

INSERT OR
REPLACE INTO Suggestion (id, semester, event)
VALUES
    /* TI */
    (1, 20211, 3),
    /* SI */
    (2, 20211, 7);