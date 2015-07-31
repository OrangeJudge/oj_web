Orange Judge
============

[![Build Status](https://travis-ci.org/OrangeJudge/oj_web.svg?branch=master)](https://travis-ci.org/OrangeJudge/oj_web)

Orange Judge is an open-source code judge application for programming problems.

This application is built for [OrangeJudge.com](https://orangejudge.com), as well as those who would like to
provide code judge services or hold coding contests locally or publically.

## Attention

Orange Judge currently is still under rapid development, and does not have a stable release yet.
We encourage you to use it for testing or study purpose only.

Some of the functions may not be working yet. You are welcome to report any issues you encounter via GitHub.

## User's Guide

### New installation

Please install or upgrade the the application using the following command.

```bash
curl http://storage.googleapis.com/orange-judge-builds/install.sh | bash
```

To run the application:

```bash
cd oj_web
chmod +x run.sh
./run.sh
```

### Upgrade from a previous version

More documentation will be added later.

### Need helps?

Orange Juice is planning to privide commericial support for those who want to run Orange Judge
themselves.

## Developer's Guide

Orange Judge is using the Play framework version `2.3.8`.

### Setup development environment

You can setup your development environment on Ubuntu 14.04 simply by running the script below.

```bash
curl https://raw.githubusercontent.com/OrangeJudge/oj_web/master/scripts_dev/setup.sh | bash
```

### Run the server

Please clone the whole repository, and run the program using command

```bash
sh scripts_dev/run.sh
```

### Set up IDE

You may refer to Play's documentation on setting up IDE. In hour project, IntelliJ IDEA is recommended.
You may directly import this project as an SBT project.

## Problem Format

`problem.json`

```
{
	"numberOfTestCases": 3,
	"totalTimeLimit": 1000,
	"default": {
		"timeLimit": 1000,
		"memoryLimit": 64,
		"specialJudge": false
	},
	"specialCases": {
		"1": {
			"input": "1.in",
			"answer": "1.ans",
			"timeLimit": 3000
		}
	}
}
```

## URL and API Design

This section is about the general rules we adopted in Orange Judge.

The AJAX APIs will be in form of:

```
/asyn/<version>/<domain>/<...>
```

The current version is `v1`. Please note that these APIs are not stateless. They are designed mainly for web
front-end's JavaScript interaction. If they are used programmatically, though not suggested, please ensure to
handle the cookies when necessarily.

Generally, the AJAX APIs will response `200` status code for "happy" situations. This is because most
front-end libraries including jQuery "prefers" to handle them.

The RESTful APIs will be in form of:

```
/api/<version>/<domain>/<...>
```

The current version is `v1`.


## Judge API

### Judge Status

|  Code |     Meaning     |
|-------|-----------------|
|  100  | Judging.        |
|  200  | Accepted.       |
|  300  | Wrong Answer.   |
|  301  | Presentation Error. 
|  400  | Compilation Error. |
|  401  | Runtime Error.  |
|  402  | Time Limit Exceeded. |
|  403  | Memory Limit Exceeded. |
|  404  | Output Limit Exceeded. |
|  405  | Restricted Function.   |
|  500  | Internal Judge Error. |

## License

Orange Judge is licensed under the Apache 2 license. Please refer to the `LICENSE` file
for more details.

