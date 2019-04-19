export APP_HOME=/home/viethoa/Documents/tima/mutil-files-word-count
export JAVA_OPTS="-Dapp.home=$APP_HOME"
java -cp "$APP_HOME/conf:$APP_HOME/target/libs/*:$APP_HOME/target/multi-file-words-count-1.0-SNAPSHOT.jar" tima.WordCountApp

