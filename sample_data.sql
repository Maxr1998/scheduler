INSERT OR
REPLACE INTO Studycourse (id, name, revision)
VALUES (1, 'B.Sc. Informatik', 'PO 2018'),
       (2, 'B.Sc. Ingenieurinformatik', 'PO 2018'),
       (3, 'B.Sc. Medizinische Informatik', ''),
       (4, 'M.Sc. Informatik', 'PO 2018'),
       (5, 'M.Sc. Ingenieurinformatik', 'PO 2016');

INSERT OR
REPLACE INTO Event (id, name, module)
VALUES (1, 'Informatik 2', ''),
       (2, 'Übung zu Informatik 2', ''),
       (3, 'Einführung in die Theoretische Informatik', ''),
       (4, 'Mathematik für Informatiker II', ''),
       (5, 'Übung zu Mathematik für Informatiker II', ''),
       (6, 'Globalübung - Mathematik für Informatiker II', ''),
       (7, 'Systemnahe Informatik', ''),
       (8, 'Übung zu Systemnahe Informatik', ''),
       (9, 'Grundlagen der Human-Computer Interaction / Multimedia Grundlagen II', ''),
       (10, 'Übung zu Grundlagen der Human-Computer Interaction / Multimedia Grundlagen II', ''),
       (11, 'Softwareprojekt', ''),
       (12, 'Übung zu Softwareprojekt', ''),
       (13, 'Ad-Hoc und Sensornetze', ''),
       (14, 'Übung zu Ad-Hoc und Sensornetze', ''),
       (15, 'Safety and Security', ''),
       (16, 'Übung zu Safety and Security', ''),
       (17, 'Ingenieurwissenschaftliche Grundlagen', ''),
       (18, 'Übung zu Ingenieurwissenschaftliche Grundlagen', ''),
       (19, 'Konstruktionslehre', ''),
       (20, 'Übung zu Konstruktionslehre', '');


INSERT OR
REPLACE INTO StudycourseEvents (studycourse, event, semester, required)
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
REPLACE INTO Schedule (semester, event, day, start_time, end_time, room)
VALUES
    /* Informatik II Tue & Thu */
    (20211, 1, 2, 615, 705, -1),
    (20211, 1, 4, 615, 705, -1),
    /* TI Mon & Thu */
    (20211, 3, 1, 975, 1065, -1),
    (20211, 3, 4, 975, 1065, -1),
    /* MFI II Mon & Tue */
    (20211, 4, 1, 615, 705, -1),
    (20211, 4, 2, 855, 945, -1);

INSERT OR
REPLACE INTO Suggestion (id, event)
VALUES
    /* TI */
    (0, 3),
    /* SI */
    (1, 7);