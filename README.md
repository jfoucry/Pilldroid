# Pilldroid

> I apologize. For know, even string exist in English, _Pilldroid_ is a French
> only application. That because the global drugs database contain only French
> drug's nommage. If you can find a way to provide another country global
> database,feel free to [contact
> me](mailto://pilldroid@foucry,net?Subject="Another database")
>
Pilldroid est une application de gestion de stock **théorique** de médicament à
destination des personnes résidant en France.

<a href="https://f-droid.org/packages/net.foucry.pilldroid">
    <img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
    alt="Get it on F-Droid"
    height="80">
</a>

## Qu'est-ce que Pilldroid ?

Pilldroid est une applicaton de gestion de stock _théorique_ de médicaments,
pour téléphone Android©.

## Pourquoi « Stock théorique » ?

Pilldroid ne sait ipas (et n'a aucun moyen de savoir) si vous avez ou non pris vos
médicaments. Pilldroid effectue, simple calcul : `stock connu - prise par
jour`. Ce calcul est fait tous les jours entre 11h et midi.


## De quelles autorisations l'application Pilldroid a-t-elle besoin ?

- Pilldroid a besoin de savoir que le téléphone a été redémarré pour elle-même
lancer son cycle de réveil journalier.
- Pilldroid a besoin d'accéder à l'appareil photo de votre téléphone pour
  scanner le code-barres des boîtes de médicaments.
- Pilldroid a besoin de pouvoir vous envoyer des notifications.
- Pilldroid a besoin de faire vibrer le téléphone pour les notifications.

## L'application Pilldroid embarque-t-elle des bibliothèques tierce ?

Oui. Pour la lecture de code-barres, Pilldroid utilise le projet
[zxing](https://github.com/journeyapps/zxing-android-embedded) lui-même libre et
ouvert.

## L'application Pilldroid contient-elle des pisteurs ?

[NON !](https://reports.exodus-privacy.eu.org/fr/reports/net.foucry.pilldroid/latest/)

## D'où viennent les données de Pilldroid ?

Elles sont issues de plusieurs fichiers de l'Agence pour la Sécurité des
Médicaments, agrégés dans une base de données grâce à script Python qui est disponible sur le dépôt [TransformMeds](https://github.com/jfoucry/TransformMeds).

## Ce que n'est pas Pilldroid

- Pilldroid n'est pas un pilulier. Pilldroid ne vous rappellera pas de prendre
vos médicaments, c'est **VOTRE** responsabilité.
- Pilldroid n'est pas fiable, ce n'est qu'une aide. Seul votre stock réel est bon.

## Comment peut-on participer à Pilldroid ?

Mon code est de piètre qualité et mes connaissances Java maigrelettes toute
amélioration **que je comprends** sera la bienvenue.

Marché de niche, Pilldroid a besoin de testeuses et testeurs.

Et si vous ne pouvez-vous pas par ces moyens, vous pouvez m'aider à payer mon
électricité et mon accès internet :
<script src="https://liberapay.com/Pilldroid/widgets/button.js"></script>
<noscript><a href="https://liberapay.com/Pilldroid/donate"><img alt="Donate using Liberapay" src="https://liberapay.com/assets/widgets/donate.svg"></a></noscript>

## L'application Pilldroid dispose telle d'un site web ?

Oui, bien sûr : [Pilldroid](https://pilldroid.foucry.net)

## Contact

Le site web en premier lieu et l'adresse mail
[pilldroid@foucry.net](mailto://pilldroid@foucry.net:Subject="Contact")
