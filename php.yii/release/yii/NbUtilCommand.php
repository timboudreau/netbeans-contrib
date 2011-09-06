<?php

/**
 * NbUtilCommand creates an Yii Web application at the specified location.
 *
 * @author Gevik Babakhani <gevik@netbeans.org>
 */
class NbUtilCommand extends CConsoleCommand {

    private $_rootPath;

    public function getHelp() {
        return <<<EOD
FOR INTERNAL USE ONLY
EOD;
    }

    public function run($args) {
    }
}