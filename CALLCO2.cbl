       IDENTIFICATION DIVISION.
       PROGRAM-ID.  "CALLCO2".
      *
      *  This program is called by CALLCO1.
      *  It returns a value "3"

       DATA DIVISION.
       WORKING-STORAGE SECTION.
       LINKAGE SECTION.
       01 PARAMETER.
                 05 VALUE1  PIC 99.

       PROCEDURE DIVISION USING PARAMETER.
       PAR-1.
           MOVE 3 TO VALUE1.
       PAR-2.
           GOBACK.

       END PROGRAM "CALLCO2".
