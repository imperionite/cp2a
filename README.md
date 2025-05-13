# cp2a

```sh
# delete maven cache libraries in main repo
rm -rf ~/.m2/repository
# clean the local repository
mvn dependency:purge-local
# Force clean and rebuild
mvn clean install
```