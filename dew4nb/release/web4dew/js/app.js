/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
// 'use strict';

// Declare app level module which depends on filters, and services
angular.module('bck2brwsr', []).
  directive('uiCodemirror', ['$timeout', function($timeout) {
        'use strict';

        var events = ["cursorActivity", "viewportChange", "gutterClick", "focus", "blur", "scroll", "update"];
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, elm, attrs, ngModel) {
                var options, opts, onChange, deferCodeMirror, codeMirror, timeoutId, val;

                if (elm[0].type !== 'textarea') {
                    throw new Error('uiCodemirror3 can only be applied to a textarea element');
                }

                options = /* uiConfig.codemirror  || */ {};
                opts = angular.extend({}, options, scope.$eval(attrs.uiCodemirror));

                onChange = function(instance, changeObj) {                    
                    val = instance.getValue();
                    $timeout.cancel(timeoutId);
                    timeoutId = $timeout(function() {
                        ngModel.$setViewValue(val);                        
                      }, 500);                    
                };
                
                deferCodeMirror = function() {
                    codeMirror = CodeMirror.fromTextArea(elm[0], opts);
                    elm[0].codeMirror = codeMirror;
                    // codeMirror.on("change", onChange(opts.onChange));
                    codeMirror.on("change", onChange);

                    for (var i = 0, n = events.length, aEvent; i < n; ++i) {
                        aEvent = opts["on" + events[i].charAt(0).toUpperCase() + events[i].slice(1)];
                        if (aEvent === void 0)
                            continue;
                        if (typeof aEvent !== "function")
                            continue;
                                                
                        var bound = _.bind( aEvent, scope );
                        
                        codeMirror.on(events[i], bound);
                    }

                    // CodeMirror expects a string, so make sure it gets one.
                    // This does not change the model.
                    ngModel.$formatters.push(function(value) {
                        if (angular.isUndefined(value) || value === null) {
                            return '';
                        }
                        else if (angular.isObject(value) || angular.isArray(value)) {
                            throw new Error('ui-codemirror cannot use an object or an array as a model');
                        }
                        return value;
                    });

                    // Override the ngModelController $render method, which is what gets called when the model is updated.
                    // This takes care of the synchronizing the codeMirror element with the underlying model, in the case that it is changed by something else.
                    ngModel.$render = function() {
                        codeMirror.setValue(ngModel.$viewValue);
                    };

                };

                $timeout(deferCodeMirror);

            }
        };
}]);

function DevCtrl( $scope, $timeout, $http ) {
    var templateHtml = 
"<h1>Please select a sample...</h1>\n";
    var templateJava = 
"package waiting4javac;\n" +
"class ToInitialize {\n" +
"  /*int*/ myFirstError;\n" +
"}\n";

    function parseJson(s) {
      if (typeof s === 'string') {
        return JSON.parse(s);
      } else {
        return s;
      }
    }

    $scope.makeMarker = function( editor, line ) {
        var marker = document.createElement("div");
        marker.innerHTML = " ";
        marker.className = "issue";
        
        var info = editor.lineInfo(line);
        editor.setGutterMarker(line, "issues", info.markers ? null : marker);
        
        return marker;
    };
    
    
    // Returns a function, that, as long as it continues to be invoked, will not
    // be triggered. The function will be called after it stops being called for
    // N milliseconds. If `immediate` is passed, trigger the function on the
    // leading edge, instead of the trailing.
    $scope.debounce = function(func, wait, immediate) {
      var timeout, result;
      return function() {
        var context = this;
        var later = function() {
          timeout = null;
          if (!immediate) result = func.apply(context);
        };
        var callNow = immediate && !timeout;
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
        if (callNow) result = func.apply(context);
        return result;
      };
    };
    
    $scope.fail = function( data ) {
        $scope.errors = eval( data );
        var editor = document.getElementById("editorJava").codeMirror;   
        editor.clearGutter( "issues" );
        
        for( var i = 0; i < $scope.errors.length; i ++ ) {
            $scope.makeMarker( editor, $scope.errors[i].line - 1 );
        }
        
    };
    
    $scope.compile = function() {
        $scope.post('compile');        
    }
    
    $scope.run = function() {
        var classes = $scope.classes;
        if (classes === null) {
            $scope.post('compile');
        } else {
            $scope.runWithClasses();
        }
    };
    
    $scope.loadResourceFromClasses = function(resource) {
        resource = resource.toString(); // from java.lang.String to JS string
        if ($scope.classes) {
            for (var i = 0; i < $scope.classes.length; i++) {
                var c = $scope.classes[i];
                if (c.className === resource) {
                    return c.byteCode;
                }
            }
        }
        return null;
    };
    
    $scope.runWithClasses = function() {
        if (!$scope.vm) {
            // initialize the VM
            var script = window.document.getElementById("brwsrvm");
            script.src = "bck2brwsr.js";
            if (!window.bck2brwsr) {
                $scope.result('<h3>Initializing the Virtual Machine</h3> Please wait...');
                $timeout($scope.run, 100);
                return;
            }
            $scope.vm = window.bck2brwsr('${project.build.finalName}.jar', $scope.loadResourceFromClasses);
        }
        var vm = $scope.vm;
        
        $scope.result("");
        $timeout(function() {
            $scope.result($scope.html);
        }, 100).then(function() {
            var first = null;
            for (var i = 0; i < $scope.classes.length; i++) {
                var cn = $scope.classes[i].className;
                cn = cn.substring(0, cn.length - 6).replace__Ljava_lang_String_2CC('/','.');
                try {
                    vm.vm._reload(cn, $scope.classes[i].byteCode);
                } catch (err) {
                    $scope.status = 'Error loading ' + cn + ': ' + err.toString();
                    break;
                }
                if (first === null) {
                    first = cn;
                }
            }   
            try {
                if (first !== null) {
                    vm.loadClass(first);
                    $scope.status = 'Class ' + first + ' loaded OK.';
                }
            } catch (err) {
                $scope.status = 'Error loading ' + first + ': ' + err.toString();
            }
        }, 100);
    };
    
    $scope.errorClass = function( kind ) {
        switch( kind ) {
            case "ERROR" :
                return "error";
            default :         
                return "warning";   
        }
    };
    
    $scope.gotoError = function( line, col ) {
        var editor = document.getElementById("editorJava").codeMirror;   
        editor.setCursor({ line: line - 1, ch : col - 1 });
        editor.focus();
    };
    
    $scope.someErrors = function() {
        return $scope.errors !== null;
    };
    
    $scope.noModification = function() {
        if (!$scope.origJava) return true;
        if (!$scope.origHtml) return true;
        if ($scope.origJava === $scope.java && $scope.origHtml === $scope.html) {
            return true;
        }
        return false;
    };

    $scope.save = function() {
        alert('Save not implemented yet');
    };
    
    $scope.result = function(html) {
        var e = window.document.getElementById("result");
        e.innerHTML = html;
    };

    $scope.goto = function() {
        var type = $scope.gototype.value;
        $scope.gototype.value="";
        $scope.javac.running = true;
        $scope.javac.postMessage({
            type : "types",
            java : type
        });
    };

    $scope.noType = function() {        
        return !$scope.gototype.value || $scope.javac.running;
    };

    
    $scope.url = "http://hg.netbeans.org/main/contrib/dew4nb/";
    $scope.description = "Development Environment for Web";
    $scope.gototype = {value : "", current : null};
    
    if (!$scope.html) {
        $scope.html= templateHtml;  
        $scope.java = templateJava;  
    }
    $scope.classes = null;
    $scope.status = 'Initializing compiler connection...';
    $scope.completions = null;
    $scope.littleCompletions = function() {
        return $scope.completions === null || $scope.completions.list === null || $scope.completions.list.length < 10;
    };
            
    (function() {
        var url = "ws://" + window.location.host + "/javac";
        var ws = new WebSocket(url);
        var javac = $scope.javac = {
            "postMessage" : function(msg) { 
                console.log('Ignoring ' + JSON.stringify(msg)); 
            }
        };
        ws.onopen = function(ev) { 
            console.log(url + ' opened'); 
            javac.postMessage = function(msg) { 
                ws.send(JSON.stringify(msg)); 
            };
            javac.onmessage({ "data" : { "status" : "Connected!" } }); 
        };
        ws.onmessage = function(ev) {
            var mev = {
                "data" : JSON.parse(ev.data)
            }
            javac.onmessage(mev); 
        };
        ws.onerror = function(ev) { 
            alert('Error: ' + ev); 
        };
        ws.onclose = function(ev) { 
            console.log(url + ' closed: ' + ev); 
        };
    })(this);

    var JAVA_WORD = /[\w$]+/;
    $scope.javaHint = function (editor, fn, options) {
        var word = options && options.word || JAVA_WORD;
        var cur = editor.getCursor(), curLine = editor.getLine(cur.line);
        var start = cur.ch, end = start;
        while (start && word.test(curLine.charAt(start - 1)))
            --start;
        var pref = start !== end && curLine.slice(start, end);
        while (end < curLine.length && word.test(curLine.charAt(end)))
            ++end;

        $scope.pendingJavaHintInfo = {callback: fn, from: CodeMirror.Pos(cur.line, start), to: CodeMirror.Pos(cur.line, end), prefix: pref};
        $scope.post('autocomplete');
    };
    $scope.applyCompletion = function(cmpltn, info) {
        var editor = document.getElementById("editorJava").codeMirror;
        editor.replaceRange(cmpltn.text, info.from, info.to);
        editor.focus();
    };
    $scope.computeCompletion = function() {
      $scope.post("autocomplete");
    };
    CodeMirror.registerHelper("hint", "clike", $scope.javaHint);

    $scope.javac.onmessage = function(ev) {
        var editor = document.getElementById("editorJava").codeMirror;
        var obj = ev.data;
        $scope.status = obj.status;
        if (obj.status === "success") {
            if (obj.type === 'getfile') {
                editor.setValue(obj.content);
                $scope.javac.context = $scope.gototype.current;
                $scope.gototype.current = null;
            }
            if (obj.type === 'types') {
                if (obj.types && obj.types.length > 0) {
                    var ctx  = obj.types[0].context;
                    $scope.gototype.current = ctx;
                    $scope.javac.postMessage({
                        type : "getfile",
                        context : ctx
                    });
                    return;
                }
            } else if (obj.type === 'autocomplete') {
                if (obj.completions) {
                    var list = obj.completions;
                    var from = editor.getCursor();
                    var to = editor.getCursor();
                    if ($scope.pendingJavaHintInfo) {
                        var list;
                        if ($scope.pendingJavaHintInfo.prefix) {
                            var pref = $scope.pendingJavaHintInfo.prefix;
                            list = [];
                            for(var i = 0; i < obj.completions.length; ++i) {
                                if (obj.completions[i].text.slice(0, pref.length) === pref)
                                    list[list.length] = obj.completions[i];
                            }
                        }
                        var render = function(elt, data, cur) {
                            var le = elt.appendChild(document.createElement("span"));
                            le.className = "Java-hint-left";
                            var name = le.appendChild(document.createElement("span"));
                            name.className = "Java-hint-name";
                            name.appendChild(document.createTextNode(cur.displayName || cur.text));
                            if (cur.extraText) {
                                le.appendChild(document.createTextNode(cur.extraText));
                            }
                            if (cur.rightText) {
                                var re = elt.appendChild(document.createElement("span"));
                                re.className="Java-hint-right";
                                re.appendChild(document.createTextNode(cur.rightText));
                            }
                        };
                        for(var i = 0; i < list.length; ++i) {
                            list[i].render = render;
                        }
                        from = $scope.pendingJavaHintInfo.from;
                        to = $scope.pendingJavaHintInfo.to;
                        $scope.pendingJavaHintInfo.callback({list: list, from: from, to: to, more: null});
                    }
                    var showHint = list.length <= 10 ? null : function() {
                        CodeMirror.showHint(editor, null, {async: true});
                    };
                    $scope.completions = {list: list.slice(0, 10), from: from, to: to, more: showHint };
                }
                $scope.pendingJavaHintInfo = null;
            } else if (obj.type === "compile") {
                $scope.errors = null;
                editor.clearGutter("issues");
                if (obj.classes !== null && obj.classes.length > 0) {
                    $scope.classes = obj.classes;
                    $scope.runWithClasses();
                } else {
                    $scope.classes = null;
                    $scope.fail(obj.errors);
                }
            } else if (obj.type === "checkForErrors") {
                if (obj.errors.length === 0) {
                    $scope.errors = null;
                    var editor = document.getElementById("editorJava").codeMirror;
                    editor.clearGutter("issues");
                } else {
                    $scope.classes = null;
                    $scope.fail(obj.errors);
                }
            }
        }
        $scope.javac.running = false;
        if ($scope.javac.pending) {
            $scope.javac.pending = false;
            $scope.post();
        }
        $scope.$apply("");
    };
    $scope.post = function(t) {
        t = t || 'checkForErrors';
        if ($scope.javac.running) {
            $scope.javac.pending = true;
        } else {
            var editor = document.getElementById("editorJava").codeMirror;
            if ($scope.computeCompletion) {
                editor.on("cursorActivity", $scope.computeCompletion);
                $scope.computeCompletion = null;
            }
            var off = editor.indexFromPos(t === 'autocomplete' && $scope.pendingJavaHintInfo ? 
                $scope.pendingJavaHintInfo.from : editor.getCursor()
            );
            $scope.javac.postMessage({
                type : t,
                context : $scope.javac.context,
                html : $scope.html,
                java :$scope.java,
                offset : off});
            $scope.javac.running = true;
            if ($scope.status.indexOf('Init') < 0) {
                $scope.status = 'Compiling...';
                $scope.$apply("");
            }
        }
        if (t !== 'autocomplete') {
            $scope.classes = null;
            $scope.errors = [];
            localStorage.java = $scope.java;
            localStorage.html = $scope.html;
        }
    };

    CodeMirror.commands.autocomplete = function(cm) {
        CodeMirror.showHint(cm, null, {async: true});
    };

    $scope.$watch( "html", $scope.debounce( $scope.post, 500 ) );
    $scope.$watch( "java", $scope.debounce( $scope.post, 500 ) );

}
