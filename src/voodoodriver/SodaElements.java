/*
Copyright 2011 Trampus Richmond. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice, this list of
      conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright notice, this list
      of conditions and the following disclaimer in the documentation and/or other materials
      provided with the distribution.

THIS SOFTWARE IS PROVIDED BY TRAMPUS RICHMOND ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
TRAMPUS RICHMOND OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the authors and 
should not be interpreted as representing official policies, either expressed or implied, of Trampus Richmond.
 
 */

package voodoodriver;

public enum SodaElements {
	WHITELIST,
	BUTTON,
	LABEL,
	H3,
	NAME,
	SCRIPT,
	FILE_FIELD,
	UL,
	OL,
	H4,
	H5,
	TIMESTAMP,
	PRE,
	SELECT_LIST,
	H6,
	WAIT,
	ACTION,
	CHECKBOX,
	P,
	RADIO,
	TABLE,
	TD,
	JAVASCRIPT,
	ASSERT,
	EXCEPTION,
	AREA,
	DIV,
	FORM,
	FRAME,
	MAP,
	TEXTAREA,
	TEXTFIELD,
	PUTS,
	CSV,
	LI,
	VAR,
	RUBY,
	LINK,
	TR,
	SELECT,
	BROWSER,
	IMAGE,
	H1,
	DIALOG,
	HIDDEN,
	SPAN,
	TEXT_FIELD,
	H2,
	FILEFIELD,
	ATTACH,
	STAMP,
	DND,
	EXECUTE,
	ARG,
	PLUGIN;
	
	static public boolean isMember(String aName) {
		boolean result = false;
		SodaElements[] values = SodaElements.values();
		
		for (SodaElements amethod : values) {
			if (amethod.name().equals(aName)) {
				result = true;
				break;
			}
		}

		return result;
	}
}



