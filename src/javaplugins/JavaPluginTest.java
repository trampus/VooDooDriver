/*
  Copyright (c) 2011, SugarCRM, Inc.
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
  notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
  * Neither the name of SugarCRM, Inc. nor the
  names of its contributors may be used to endorse or promote products
  derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
  ARE DISCLAIMED. IN NO EVENT SHALL SugarCRM, Inc. BE LIABLE FOR ANY
  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package javaplugins;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import voodoodriver.SodaBrowser;
import voodoodriver.VDDPluginInterface;

/**
 * a simple example Java plugin for use with VDD. This plugin demonstrates VDD's functionality of interacting with WebElement, and performing
 * actions that are not possible with standard VDD elements.
 * 
 * @author Lehan Huang
 */
public class JavaPluginTest implements VDDPluginInterface {

	@Override
	public int execute(String[] args, SodaBrowser browser, WebElement element) {
		System.out.printf("(*)Plugin: Starting VDD JavaPluginTest...\n");
		
		if (args == null && element == null){
			return 1;
		}
		else if (args == null){
			element.findElement(By.id("text1")).clear();
			element.findElement(By.id("text1")).sendKeys("voodoo");
		}
		else{
			System.out.printf("(*)Plugin: arg size: "+args.length+"\n");
			element.findElement(By.id("text1")).clear();
			element.findElement(By.id("text1")).sendKeys(args[0]);
		}
		
		System.out.printf("(*)Plugin: JavaPluginTest finished.\n");
		
		return 0;
	}

}
