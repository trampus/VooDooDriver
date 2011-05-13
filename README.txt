This is a java based replacement for SODA: https://github.com/trampus/SODA

This project is currently under dev and isn't ready for use yet.


Notes:

(*)Browser Closing:
   Currently if you are using the --test command line option the browser you are using will be closed
   for you after each test is run even if you do now call the SODA command <browser action="close" />.  
   This is not the case of SODA suites.  SODA suites keep the browser open until the end of the suite.
   