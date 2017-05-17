WORKDIR=/Users/shawnyhw6n9/Documents/myProject/CAP4/cap-web/src/main/webapp/static
cd $WORKDIR
node ../static/requirejs/2.3.2/r.js -o build.js optimize=uglify name=main baseUrl=../static out=../static/main-built.js
