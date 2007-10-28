;;; find-file-ide.el --- edit current file in ide

;; Author: Sandip V. Chitale
;; Created: May 05 2004
;; Keywords: open file ide

;; This file is not part of GNU Emacs yet.

;;; Commentary:
;;
;; This package enables opening file in IDE  from emacs buffer.
;; 
;; Background:
;; Eclipse (http://www.eclipse.org)
;; 
;; NetBeans (http://www.netbeans.org)
;;
;; IntelliJ (http://www.jetbrains.com)
;;
;; IntelliJ has an easy mechanism to edit files in external programs. For example, defining an external tool
;; (using Options:External Tools) with:
;;
;; Program: C:\emacs\bin\gnuclientw.exe
;; Parameters: +$LineNumber$  "$FilePath$"
;;
;; However there is no easy way to open a file in IntelliJ from emacs.
;;
;; Alexey Efimov has written an IntelliJ plugin called FileAssociation
;; The FileAssociations plugin provide special file server.
;; It load files into IntelliJ from outside, using a TCP/IP socket based
;; server. Get it from:
;; http://www.intellij.org/twiki/bin/view/Main/FileAssociations
;; 
;; You may have to customize the `intellij-install-dir' variable to specify IntelliJ installation
;; directory.
;;
;; Installation:
;; To install and use, put this file on your Emacs-Lisp load path and add the
;; following into your ~/.emacs startup file:
;;
;;  (require 'find-file-ide)
;;
;; You may set the following key binding:
;;
;; (global-set-key [(control e)] 'find-file-ide)
;;
;;; Code:
(require 'working)

(defgroup find-file-ide nil
  "find-file-ide group."
  :prefix "find-file-ide-"
  :group 'find-file)

(defcustom find-file-ide-function 'find-file-in-eclipse
  "Define the function for opening the file in ide. Default value us `find-file-in-eclipse'. Other possible value is
`find-file-in-intellij'."
  :type '(function :tag "Function to open file in ide")
  :group 'find-file-ide)

(defcustom find-file-ide-server-host-name "127.0.0.1"
  "IDE File Open Server host name."
  :type 'string
  :group 'find-file-ide)

(defcustom find-file-ide-server-port-number 4050
  "IDE File Open Server posrt number"
  :type 'integer
  :group 'find-file-ide)

;;; common
(defun current-line ()
  "Return current line number. Based on code from `what-line'."
  (let ((opoint (point)) start)
    (save-excursion
      (save-restriction
	(goto-char (point-min))
	(widen)
	(forward-line 0)
	(setq start (point))
	(goto-char opoint)
	(forward-line 0)
	(count-lines 1 (point))))))

;;; eclipse or netbeans or intellij
(defun start-ide ()
  "Start the ide process."
  (interactive)
  (if (not (is-ide-running))
      (start-process "IDE"
		     "*ide*"
		     "/home/sc32560/work/sun/visualweb/cvsworkspaces/trunk/nbbuild/netbeans/bin/netbeans")))

(defun is-ide-running ()
  "Check if the IDE file open server is up."
  (interactive)
  (condition-case error
      (progn
	(delete-process (open-network-stream "IDEFileOpenServer" nil find-file-ide-server-host-name find-file-ide-server-port-number))
	t)
    (error nil)))

(defun find-file-in-eclipse-directly (filename)
  "Edit file FILENAME in IDE directly."
  (interactive "FFind file in IDE: ")
  (let* ((matched (string-match "\\(..*\\):\\([0-9]+\\):?" filename))
	 (linenum 1)
	 )
    (if matched
	(progn
	  (message "File with line number.")
	  (setq linenum  (string-to-number (match-string 2 filename)))
	  (setq filename (match-string 1 filename))))
    (find-file-in-eclipse-impl filename linenum)))

(defun find-file-in-eclipse-impl (file &optional line col)
  "Implementation for Eclipse."
  (interactive)
  (if (not (is-ide-running))
      (progn
	(start-ide)
	(working-status-forms "Starting IDE." "done"
	  (while (not (is-ide-running))
	    ;; Use default buffer position.
	    (working-dynamic-status nil)
	    (sleep-for 0.05))
	  (working-dynamic-status t))))
  (let ((fileopenserver (open-network-stream "IDEFileOpenServer" nil find-file-ide-server-host-name find-file-ide-server-port-number)))
    (process-send-string fileopenserver (concat
					 "+"
					 (number-to-string (or line 1))
					 ":"
					 col
					 " "
					 file))
    (process-send-eof fileopenserver)
    (process-kill-without-query fileopenserver)))

(defun find-file-dired-file-in-eclipse()
  "Open file on current line in Eclipse."
  (interactive)
  (find-file-in-eclipse-impl (expand-file-name (dired-filename-at-point)) 1))

(defun find-file-buffer-file-in-eclipse()
  "Open current file in Eclipse."
  (interactive)
  (find-file-in-eclipse-impl (expand-file-name (buffer-file-name))
			     (current-line)
			     (number-to-string (- (point) (line-beginning-position)))))

(defun find-file-in-eclipse()
  "Open file in Eclipse."
  (interactive)
  (if (eq major-mode 'dired-mode)
      (find-file-dired-file-in-eclipse)
      (find-file-buffer-file-in-eclipse)))

;;; intellij
(defgroup IntelliJ nil
  "IntelliJ group."
  :group 'find-file-ide
  :prefix "intellij-")

(defcustom intellij-install-dir "C:\\IntelliJ-IDEA-4.0"
  "IntelliJ installation directory."
  :type 'directory
  :group 'IntelliJ)

(defun find-file-in-intellij-impl (file &optional line col)
  "Open the specified file in Intellij. If specified go to that line
and column."
  (shell-command (format "%s\\jre\\bin\\javaw -classpath \"%s\\plugins\\FileAssociations-bin.jar\" loader \"%s\" -l %s -c %s"
			 intellij-install-dir
			 intellij-install-dir
			 file
			 (or line
			     1)
			 (or col
			     1))
		 "*Messages*"
		 "*Messages*"))
(defun find-file-dired-file-in-intellij ()
  ""
  (interactive)
  (find-file-in-intellij-impl (expand-file-name (dired-filename-at-point)) 1))

(defun find-file-buffer-file-in-intellij ()
  "Open current file in Intellij and go to the current line. With arg
go to current column."
  (interactive "P")
  (find-file-in-intellij-impl (expand-file-name (buffer-file-name)) (current-line)))

(defun find-file-in-intellij()
  ""
  (interactive)
  (if (eq major-mode 'dired-mode)
      (find-file-dired-file-in-intellij)
      (find-file-buffer-file-in-intellij)))

;;;
(defun find-file-ide ()
  ""
  (interactive)
  (funcall find-file-ide-function))

(provide 'find-file-ide)
;;; end of find-file-ide.el