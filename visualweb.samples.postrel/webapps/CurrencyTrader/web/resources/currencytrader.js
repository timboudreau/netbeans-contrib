/*
* Copyright 2007 Sun Microsystems, Inc. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* - Redistributions of source code must retain the above copyright
*   notice, this list of conditions and the following disclaimer.
*
* - Redistribution in binary form must reproduce the above copyright
*   notice, this list of conditions and the following disclaimer in
*   the documentation and/or other materials provided with the
*   distribution.
*
* Neither the name of Sun Microsystems, Inc. or the names of
* contributors may be used to endorse or promote products derived
* from this software without specific prior written permission.
*
* This software is provided "AS IS," without a warranty of any
* kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
* WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
* EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
* SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
* DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN
* OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
* FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
* PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF
* LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE,
* EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
*
* You acknowledge that Software is not designed, licensed or intended
* for use in the design, construction, operation or maintenance of
* any nuclear facility.
*/

/** JavaScript for the Currency Trader sample application. */

/** Delay between requests to the server when polling. */
var pollDelay = 2500;

/** 
  * Whether we will include the "closure" option in our invocations of DynaFaces.fireAjaxTransaction when polling.
  * If true, we send the next poll request by executing the closure. This allows us to send the next poll request not only 
  * upon receiving the ajax response (the normal case), but also in the event that the ajax response is not received
  * within the verifyPollResponseDelay interval (due to a temporary network problem, for instance).
  */
var useClosure = false;

/** Delay between the time we send a poll request and the time we manually execute the closure to verify that the response was received. **/
var verifyPollResponseDelay = 15000;

/** Timestamp for when the "pending" poll request was sent. */
var pendingPollRequestTimestamp;

/** Whether polling should continue. Used by customPostReplace. */
var continuePolling = false;

/** 
  * Invoked after receiving an Ajax response and replacing elements in the DOM. Sends a poll request if continuePolling is true.
  * @param element The element we just replaced with new markup.
  * @param markup The markup with which we just replaced the element.
  * @param closure A closure that, when executed, sends the next poll request.
  */
function customPostReplace(element, markup, closure) {
    if (element == document.getElementById('form1:exchangeRateTable')) {
        if (continuePolling == true) {
            if (useClosure) {
                //execute the closure to send the next poll request
                closure();
            } 
            else {
                //send the next poll request without executing the closure
                setTimeout(poll, pollDelay);
            }
        }
    }
    //invoke default behavior
    markup.evalScripts();
}

/** 
  * Start polling the server, with the delay between requests determined by the pollDelay variable. 
  * This function is called in the body's onload event handler.
  */
function startPolling() {
    continuePolling = true;
    poll();
}

/** Stop polling the server. This function is called by the body's onunload event handler. */
function stopPolling() {
    continuePolling = false;
}

/** 
  * Poll the server by sending an Ajax request.
  * Send the hiddenPollIndicator hidden field as input so the server knows this is a poll request. (See Page1.prerender.)
  * Do not execute over any server-side nodes. We are just polling to get the latest exchange rates, not to process any input.
  * Rerender the exchangeRateTable and the openPositionTable.
  * Upon rerendering, use the customReplace function to perform replacing of elements, and the
  * customPostReplace function to send the next poll request.
  * If useClosure is true, attach a closure function to this Ajax request and execute it both
  * in customPostReplace and after the verifyPollResponseDelay: one of these invocations
  * will cause the next poll request to be sent.
  */
function poll() {
    if (useClosure) {
        var thisPollRequestTimestamp = new Date().getTime();
        //pass thisPollRequestTimestamp to getClosureThatSendsNextPollRequest so that the closure will know
        //the timestamp of this poll request.
        DynaFaces.Tx.config.pollTx.closure = getClosureThatSendsNextPollRequest(thisPollRequestTimestamp);
        pendingPollRequestTimestamp = thisPollRequestTimestamp;
    }
    DynaFaces.Tx.fire('pollTx');
    if (useClosure) {
        //execute the closure manually after the verifyPollResponseDelay, so that the next poll request will be sent even if the
        //response for this poll request is never received.
        setTimeout(DynaFaces.Tx.config.pollTx.closure, verifyPollResponseDelay);
    }
}

/** 
  * Get a closure to be associated with a poll request. When the inner function executes, it checks whether
  * its poll request is the one that is considered "pending," and, if so, it sends the next poll request.
  * @param thisPollRequestTimestamp The timestamp of the poll request associated with this closure.
  * @return An inner function that sends the next poll request if the timestamp of the poll request associated with this closure
  * matches the timestamp of the "pending" poll request.
  */ 
function getClosureThatSendsNextPollRequest(thisPollRequestTimestamp) {
    return function() {
        if (pendingPollRequestTimestamp == thisPollRequestTimestamp) {
            //we have arrived here for one of two reasons:
            //1. the normal case: this code is executing in the customPostReplace function upon receiving the response for the "pending" Ajax request.
            //2. alternate case: this code is executing via the setTimeout call in the poll function, and no response for the "pending" Ajax request has been received.
            //in either case (1 or 2), send the next poll request.
            setTimeout(poll, pollDelay);
        }
    }
}

/**
  * Collect the data to send with Ajax requests from Ajax zones.
  * In addition to the default collectPostData behavior, add the hiddenRenderId name/value pair.
  * @param ajaxZone The zone which is sending an Ajax request.
  * @param element The element the user has activated, causing the Ajax request to be sent.
  * @param outArray An array of name/value pairs to be populated; these will be sent with the Ajax request.
  */
function customCollectPostData(ajaxZone, element, outArray) {
    var name = 'form1:hiddenRenderId';
    var value = document.getElementById(name).value;
    outArray.push(name+'='+value);
    DynaFacesZones.collectPostData(ajaxZone, element, outArray);
}

/** An array of id suffixes we will look for when invoking customReplace. */
var idSuffixesToReplace = [':currentPriceColumn:currentPriceText',':currentPriceColumn:currentPriceImage',':floatingProfitColumn:floatingProfitText',':floatingProfitColumn:floatingProfitImage'];

/**
  * Function to perform custom replacing of elements.
  * This function is specified as the replaceElement option of fireAjaxTransaction, which is used to poll the server.
  * If the id parameter is that of the openPositionTable, extract from the src parameter nodes that represent the contents of the current price column and floating profit column, and replace the corresponding existing nodes in the page with the extracted nodes.
  * We do this so that other columns of the openPositionTable are not replaced, especially the close amount, which contains a dropdown.
  * If we were to replace the dropdown, it could interfere with a user's interacting with the dropdown.
  * If the id parameter is not that of the openPositionTable, simply call through to the default replaceElement function.
  * @param id The id of the element we are replacing as part of a call to fireAjaxTransaction.
  * @param src The new markup for the element.
  */
function customReplace(id, src) {
    if (id == 'form1:openPositionZone:openPositionTable') {
        //create a temporary div
        var temp = document.createElement('div');

        //populate the temporary div with the src markup
        temp.innerHTML = DynaFaces.trim(src);

        //temp.firstChild is the src markup's root node
        var revisedOpenTableNode = temp.firstChild;

        //find and replace specific elements in the current price and floating profit columns
        replaceNodesByIdSuffix(revisedOpenTableNode, idSuffixesToReplace);
    }
    else {
        //just call the default replaceElement function
        Element.replace(id, src);
    }
}

/** 
  * For each id suffix in suffixArr, extract any nodes within the supplied element having that id suffix.
  * Then replace the corresponding existing nodes in the page with the extracted nodes.
  * @param element The parent node from which to extract nodes with the specified suffixes. Here, element will be a new form1:openPositionZone:openPositionTable node.
  * @param suffixArr An array containing the id suffixes of nodes to extract from element and subsequently use for replacement.
  */
function replaceNodesByIdSuffix(element, suffixArr) {
    for (var i = 0; i < suffixArr.length; i++) {
        var nodeArr = [];

        //look for nodes in element whose id ends with suffixArr[i], and append those nodes to nodeArr
        findNodesByIdSuffix(element, suffixArr[i], nodeArr);

        //replace existing nodes in the page with corresponding nodes in nodeArr
        replaceNodes(nodeArr);
    }
}

/**
  * Search element and its children for nodes whose id ends with suffix, and append those nodes to nodeArr.
  * @param element The element to search.
  * @param suffix The id suffix for which to search.
  * @param nodeArr The array to which we append nodes.
  */
function findNodesByIdSuffix(element, suffix, nodeArr) {
    if (element.id) {
        if (endsWith(element.id, suffix)) {
            nodeArr.push(element);
        }
    }
    if (element.hasChildNodes()) {
        for (var i = 0; i < element.childNodes.length; i++) {
            findNodesByIdSuffix(element.childNodes[i], suffix, nodeArr);
        }
    }
}

/**
  * Replace existing nodes in the page with the corresponding nodes from the supplied nodeArr.
  * @param nodeArr The array of nodes.
  */
function replaceNodes(nodeArr) {
    for (var i = 0; i < nodeArr.length; i++) {
        //find the corresponding existing node in the page, namely, the one whose id is the same as nodeArr[i].id
        var correspNode = document.getElementById(nodeArr[i].id);
        if (!correspNode) {
            continue;
        }

        //replace the existing node in the page with nodeArr[i]
        correspNode.parentNode.replaceChild(nodeArr[i], correspNode);
    }
}

/**
  * The custom inspectElement function for openAPositionPanelZone.
  * If the supplied element is not an INPUT element, return false.
  * Otherwise, return the result of the default inspectElement function.
  * This function is necessary to prevent an Ajax request from being sent when the user interacts with any of the dropdowns in openAPositionPanelZone.
  * Note: openAPositionPanelZone contains the controls to open a position (not to be confused with openPositionZone, which shows all the currently open positions).
  * @param element The element to inspect so as to determine if it should send Ajax requests. This will be openAPositionPanelZone and its children.
  * @return False if element is not an INPUT element, or the result of the default inspectElement function otherwise.
  */
function customInspectElementForPanelZone(element) {
    return DynaFacesZones.inspectElementByNameAndAttribute(element, 'input');
}

/** 
  * Determine if the supplied str ends with the supplied suffix.
  * @param str The full string.
  * @param suffix The suffix that str might end with.
  * @return True if str ends with suffix, false otherwise.
  */
function endsWith(str, suffix) {
    var start = str.length - suffix.length;
    if (start < 0) {
        return false;
    }
    return str.substring(start) == suffix;
}