# Pilldroid

Pilldroid est une application de gestion de stock **théorique** de médicament à
destinations des personnes résidant en France.

## Ce que n'est pas Pilldroid

- Pilldroid n'est pas un pilulier. Pilldroid ne vous rappellera pas de prendre
vos médicaments, c'est **VOTRE** responsabilité ;

- Pilldroid n'est pas fiable, ce n'est qu'une aide. Seul votre stock réel est bon
;

- Pilldroid ne vous surveille pas.

## Pourquoi « Stock théorique » ?

Pilldroid ne sait (et n'a aucun moyen de savoir) si vous avez ou non pris vos
médicaments. Pilldroid effectue, simple calcul : `stock connus - prise par
jour`. Ce calcul est fait tous les jours entre 11h et midi.

## De quelles autorisations l'application Pilldroid a-t-elle besoin ?

- Pilldroid à besoin de savoir que le téléphone a été redémarré pour elle-même
lancer son cycle de réveil journalier ;
- Pilldroid a besoin d'accéder à l'appareil photo de votre téléphone pour
  scanner le code barre des boites de médicaments ;
- Pilldroid a besoin de pouvoir vous envoyer des alarmes ;
- Pilldroid a besoin de faire vibrer le téléphone pour les alarmes ;
- Pilldroid utilise un accès Internet pour les remontée de crash *via* la bibliothèque [sentry.io](https://sentry.io).
  
## L'application Pilldroid embarque-t-elle des bibliothèques tierce ?

Oui. Pour la lecture de code barre, Pilldroid utilise le projet
[zxing](https://github.com/journeyapps/zxing-android-embedded) lui-même libre et
ouvert.

A terme Pilldroid utilisera la bibliothèque
[sentry.io](https://sentry.io/for/android/) quand j'aurais compris comment cela
fonctionne. Le but est d'avoir des remontées les crashs de l'application sans
collecter de données à caractère personnel.
Déjà embarquée mais non fonctionnelle, `sentry.io` à besoin de l'accès Internet.

## L'application Pilldroid contient-elle des pisteurs ?

[NON !](https://reports.exodus-privacy.eu.org/fr/reports/234531/)

## D'où viennent les données de Pilldroid ?

Elles sont issues de plusieurs fichiers de l'Agence pour la Sécurité des
Médicaments, agrégés dans une base de données grâce à script Python qui sera
prochainement mis sur mon compte `github`.

## Comment peut-on participer à Pilldroid ?

Mon code est de piètre qualité et mes connaissances Java maigrelettes toute
amélioration **que je comprends** sera la bien venue (je n'ai pas pigé les
`lambda` par exemple).

Marché de niche, Pilldroid a besoin de testeuses et testeurs.

Prochainement, vous pourrez glisser quelques menues monnaies dans une tirelire
en ligne.

## L'application Pilldroid dispose telle d'un site web ?

Oui, [Pilldroid](https://pilldroid.foucry.net)
