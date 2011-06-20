VooDooDriver:
   This is a java based replacement for SODA: https://github.com/trampus/SODA

This project is currently under dev and isn't ready for use yet.

(*)Things that are different from Ruby Soda:
1.) The row soda element is no longer supported, you need to use "tr" now.  It just makes more since.
2.) The cell soda element is no longer supported, you need to use "td" now.  It just makes more since.
3.) <a href> links can not be found in a TR unless the TR owns the link, if the link is owned by a TD then
   you have to parent the link with the proper td.
4.) When using set for text fields in ruby soda, soda would clear the field of any existing text before 
   setting the new value.  Soda java does not do this, as it will just append text to the current field.
5.)VooDooDriver will only allow you to find an element by one selector where Soda ruby would allow you more then one.  
6.)VooDooDriver now supports accessing elements using css selectors.  Example:
<button css="input[type=button][value='Search']" />
7.)VooDooDriver supports plugins.  Please see the Plugin.txt doc.
8.)VooDooDriver supports storing off dragable HTML elements.  THis can be done by using the "save" attribute for supported soda
elements.  Example:
   <div id="foo" save="my-div" />, this stores the div element under the ref "my-div", which can only be used by the <dnd>
   soda command.  See Drag'n Drop example for more info.


Notes:

(*)Browser Closing:
   Currently if you are using the --test command line option the browser you are using will be closed
   for you after each test is run even if you do now call the SODA command <browser action="close" />.  
   This is not the case of SODA suites.  SODA suites keep the browser open until the end of the suite.
   