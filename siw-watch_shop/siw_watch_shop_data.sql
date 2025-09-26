--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

-- Started on 2025-09-26 02:23:58

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4958 (class 0 OID 39566)
-- Dependencies: 226
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.orders (total_price, creation_time, id, user_id, code, status) FROM stdin;
50	2025-09-23 21:40:13.931966	202	552	ORD-1758656413927-2954	EFFETTUATO
50	2025-09-23 22:26:02.733667	252	453	ORD-1758659162728-2522	EFFETTUATO
0	\N	403	702	\N	IN_CREAZIONE
510000	2025-09-24 22:23:25.80043	402	702	ORD-1758745405794-0108	EFFETTUATO
0	\N	203	552	\N	IN_CREAZIONE
0	\N	302	453	\N	IN_CREAZIONE
40100	2025-09-25 00:56:57.272716	552	602	ORD-1758754617264-4285	EFFETTUATO
0	\N	554	602	\N	IN_CREAZIONE
8000	2025-09-25 00:57:10.926706	553	602	ORD-1758754630926-3221	EFFETTUATO
96000	2025-09-25 01:04:14.166689	555	753	ORD-1758755054164-8581	EFFETTUATO
0	\N	557	753	\N	IN_CREAZIONE
30060	2025-09-25 01:04:41.205297	556	753	ORD-1758755081205-6981	EFFETTUATO
0	\N	602	652	\N	IN_CREAZIONE
17000	\N	352	652	\N	IN_CREAZIONE
\.


--
-- TOC entry 4960 (class 0 OID 39579)
-- Dependencies: 228
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (current_order_id, id, name, surname) FROM stdin;
\N	452	Lorenzo	Alicandro
\N	502	admin	admin
203	552	matteo	fiore
302	453	matteo	torre
403	702	Matteo	Agostino
\N	752	Dora	Rossi
554	602	francesco	marzano
557	753	Matteo	Gallo
602	652	Lorenzo	Alicandro
\.


--
-- TOC entry 4956 (class 0 OID 39551)
-- Dependencies: 224
-- Data for Name: credentials; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.credentials (id, user_id, email, password, role, username) FROM stdin;
453	453	ma@gmail.com	$2a$10$EDfu7aPZcCjsOFlSjD4wguUfSRsxuOOGOkpX01I4mXvwoaJQzgM0a	USER	matt
502	502	admin0@gmail.com	$2a$10$8aM55noL3wQ2eWxoC9QqwuliuVu.aEPrk1/9ZvgikKTAfG2JODMCm	ADMIN	admin0
452	452	lorenzo.alicandro05@gmail.com	\N	ADMIN	sosa
552	552	matteofiore@gmail.com	$2a$10$dCs1UWCEEZH5sUd6qVkoyuIECyBzFLXARhDAIDeZSFx3TJg3wg3ey	USER	matteus
602	602	francescomarzano@gmail.com	$2a$10$i7sCayqyotprc7RZ.R4yxOF1W18uK7pBdpE3Yp94RXZUDn2VnBbGy	USER	marzaz
652	652	lorenzo.alicandro0@gmail.com		USER	loren
702	702	matteoagostino@gmail.com	$2a$10$8U9Iul9WJEERWOOOkcCSXudgE0bvx8p1.GbESiZJapoIk2B4jY/MC	USER	mat
752	752	dorarossi@gmail.com	$2a$10$g8uYLI9E8KxNIuPolCOYCO1GvqkHm7v/BcWHMDwnIBJDCe8zSMK2y	USER	doritas
753	753	matteogallo@gmail.com	$2a$10$o7YHx.F7ql.Z/iEo4v/lL.IincExAmKbHQ55psLx.uORnvGXN9lAm	USER	gallo
\.


--
-- TOC entry 4961 (class 0 OID 39588)
-- Dependencies: 229
-- Data for Name: watch; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.watch (availability, price, stock, year, id, brand, description, image_path, name, average_rating, rating_count, order_id) FROM stdin;
t	30	20	2000	411	CASIO	total black	560b4cb7-c994-455e-8792-98810f31f2ae.jpg	VINTAGE BLACK	\N	\N	\N
t	40	20	2008	413	CASIO	buono in acciaio siderato	c680fd50-051b-4a96-b798-093322a5e4f5.webp	VINTAGE GREY	\N	\N	\N
t	8000	20	2010	414	CARTIER	orologio di lusso cartier	71e3bf01-8cd2-4f38-bfd1-aabf4a6c431b.webp	PANTHERE	\N	\N	\N
t	5000	20	2009	417	CARTIER	baignore oro 18 carati	917f338b-9e1e-4333-9a99-9d19632bae5e.webp	BAIGNOIRE	\N	\N	\N
t	50000	20	2004	420	OMEGA	speedmaster omega oro 24 carati	4d1c5cc5-f81d-41ff-80a9-08e660a14065.webp	SPEEDMASTER	\N	\N	\N
t	40000	10	2000	352	ROLEX	nuovo rolex	c09e0d42-c450-4dac-8802-98f33e2f3072.webp	DATEJUST 41	4	1	\N
t	25	20	1999	412	CASIO	rosa con stile moderno	5e1e4509-7c8e-4ca2-b647-ef392a61fcf2.webp	PINKY	5	1	\N
t	300	20	2003	418	OMEGA	classic della omega	01b57e1f-afe1-4abe-97fa-4f2fc35ea3dd.webp	1521 CLASSIC	2	1	\N
t	17000	20	1998	403	ROLEX	day date fatto d'oro 24 carati	426d5748-6bee-418b-b0bd-f1cf60b284ca.webp	DAY DATE 40	5	1	\N
t	50000	20	2008	410	PHILIPPE PATEK	luxgold con oro rosa	e358f8d0-9ad2-4727-94ad-4782d4b577d2.jpg	LUXGOLD	5	1	\N
t	4000	20	2007	416	CARTIER	orologio cartier con righiera blu	ef175ff3-1472-429b-a13d-dc699e200dca.webp	SANT-DUMOT	1	1	\N
t	30000	20	1998	409	PHILIPPE PATEK		165d23e3-569d-457c-b03a-288f564dd52c.jpg	TRINITY	4	1	\N
t	30000	20	1999	406	ROLEX	design acquatico	7e029a7a-c987-4c94-b317-7f08bf01d8cb.webp	DEEPSEA DWALLER	3	1	\N
t	50000	20	2005	404	ROLEX	daytona oro bianco	2da760e8-2cc8-4478-8efc-75c16ca3b7f8.webp	DAYTONA 11503	5	1	\N
t	50	\N	1999	302	CASIO	\N	1f578a18-7c77-4078-92a5-60b76a425f09.jpg	RETRO XR	3	2	\N
t	10000	20	2003	402	ROLEX	rolex se dwaller pulito	48feda13-f656-4a5e-9055-fbf5b0f1f74e.webp	SEA DWELLER	5	2	\N
t	900	20	2000	419	OMEGA	omega dettagli mare	ccd559c1-c689-4cb2-8c02-3f589eee7bc3.webp	1521 OCEAN	3	1	\N
t	3500	20	2004	415	CARTIER	orologio cartier tank nero 	f7848ed1-61de-4763-b5a8-48f678bc3231.webp	TANK MUST	4.5	2	\N
t	40000	20	2003	407	PHILIPPE PATEK	Philippe Patek in argento	a374034d-cd1a-4d58-9142-ac6b99b521ba.webp	NAUTILUS	5	1	\N
t	12000	20	2023	408	PHILIPPE PATEK	no descrizione	c944eb1b-012b-433b-b944-56f4e935bd52.jpg	DRAINMASTER	4	1	\N
t	20000	20	2000	405	ROLEX	gtm master in argento	14846ed3-179d-4c0e-bdc0-432da6a937c1.webp	GTM-MASTER	\N	\N	\N
t	6000	20	1989	421	OMEGA	omega	1043672b-fcc4-41f5-926a-025815cf27e5.jpg	PLANET	\N	\N	\N
t	5800	20	1997	422	IWC	pilot molto buono	1c549bab-db5a-4c0f-b483-76e092bffe15.webp	PILOTS	\N	\N	\N
t	2000	20	1999	424	IWC		d5c28ac5-e8d3-41c5-b658-7816adecff33.webp	SCHAFFHAUSEN	\N	\N	\N
t	8000	30	2005	425	TUDOR		d2109dad-5af9-4514-bc28-d8e13af39da7.webp	ROYAL 	\N	\N	\N
t	70	20	1994	502	CASIO	casio verde acqua	b3b5d25c-5870-4d1d-b24e-082e5b52f93a.jpg	AQUA	\N	\N	\N
t	30000	20	1970	427	BREITLING	navtimer svizzero	52a3dfe5-9c3b-47dd-80a4-a37112cb5921.jpg	NAVTIMER	4	1	\N
t	4000	20	2000	452	BREITLING	prest	6b8e4199-a7b5-4931-997b-b3ceedb7a3cc.png	CHRON	5	1	\N
t	48000	20	2005	426	TUDOR	blackday stile red	080b4130-c555-4e5c-8a34-ce617f6f970a.webp	BLACK DAY	3.5	2	\N
\.


--
-- TOC entry 4957 (class 0 OID 39561)
-- Dependencies: 225
-- Data for Name: order_line; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.order_line (quantity, unit_price, id, order_id, watch_id, watch_brand, watch_description, watch_image_path, watch_name, watch_year) FROM stdin;
1	50	352	202	302	\N	\N	\N	\N	\N
1	50	402	252	302	\N	\N	\N	\N	\N
2	30	658	556	411	CASIO	total black	560b4cb7-c994-455e-8792-98810f31f2ae.jpg	VINTAGE BLACK	2000
1	10000	453	402	402	ROLEX	rolex se dwaller pulito	48feda13-f656-4a5e-9055-fbf5b0f1f74e.webp	SEA DWELLER	2003
1	30000	659	556	406	ROLEX	design acquatico	7e029a7a-c987-4c94-b317-7f08bf01d8cb.webp	DEEPSEA DWALLER	1999
1	300	702	352	418	OMEGA	classic della omega	01b57e1f-afe1-4abe-97fa-4f2fc35ea3dd.webp	1521 CLASSIC	2003
2	250000	452	402	\N	ROLEX	rolex submarine date, design di luss	7ce6af6d-310d-40dd-8901-a480c9eb2051.webp	SUBMARINE DATE	1999
1	17000	653	352	403	ROLEX	day date fatto d'oro 24 carati	426d5748-6bee-418b-b0bd-f1cf60b284ca.webp	DAY DATE 40	1998
1	40000	654	552	407	PHILIPPE PATEK	Philippe Patek in argento	a374034d-cd1a-4d58-9142-ac6b99b521ba.webp	NAUTILUS	2003
2	50	655	552	302	CASIO	\N	1f578a18-7c77-4078-92a5-60b76a425f09.jpg	RETRO XR	1999
1	8000	656	553	414	CARTIER	orologio di lusso cartier	71e3bf01-8cd2-4f38-bfd1-aabf4a6c431b.webp	PANTHERE	2010
2	48000	657	555	426	TUDOR	blackday stile red	080b4130-c555-4e5c-8a34-ce617f6f970a.webp	BLACK DAY	2005
\.


--
-- TOC entry 4959 (class 0 OID 39574)
-- Dependencies: 227
-- Data for Name: orders_watches; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.orders_watches (order_id, watches_id) FROM stdin;
\.


--
-- TOC entry 4962 (class 0 OID 47858)
-- Dependencies: 230
-- Data for Name: review; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.review (id, star_rating, text, user_id, watch_id, created_at) FROM stdin;
802	1	onestissimo	552	302	2025-09-23 21:41:33.845076
852	5	molto bello, veramente.	652	352	2025-09-24 17:45:21.49538
902	5	bello	652	402	2025-09-25 00:20:28.597522
904	4	molto bello	752	427	2025-09-25 00:46:46.062085
905	5	bello come un fior d'arancio	752	452	2025-09-25 00:47:12.894135
906	5	ROSA è BELLISSIMO	752	412	2025-09-25 00:47:35.639911
907	2	brutto per il prezzo	752	418	2025-09-25 00:48:02.078361
908	5	IL MIO PREFERITO	752	403	2025-09-25 00:48:42.778547
909	3	bello rosso	752	426	2025-09-25 00:49:00.552706
910	5	molto bello	602	410	2025-09-25 00:49:47.502065
911	4	carino	602	415	2025-09-25 00:50:03.884479
912	1	prezzo troppo alto	602	416	2025-09-25 00:54:45.308861
913	4	molto elegante come piace a me	602	409	2025-09-25 00:55:14.744196
914	4	carino	602	408	2025-09-25 00:55:35.374609
915	3	nulla di particolare	602	406	2025-09-25 00:56:02.326496
916	5	fantastico	602	404	2025-09-25 00:56:23.690689
917	4	ci sta dai	602	426	2025-09-25 00:57:50.949064
918	5	molto vecchio ma sempre bello	753	302	2025-09-25 00:59:56.537094
919	5	carino	753	402	2025-09-25 01:00:59.865004
920	3	ci sta	753	419	2025-09-25 01:02:16.107313
921	5	bello	753	415	2025-09-25 01:03:19.062326
922	5	utilissimo	753	407	2025-09-25 01:20:52.831734
\.


--
-- TOC entry 4950 (class 0 OID 38713)
-- Dependencies: 218
-- Data for Name: watch_image_urls; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.watch_image_urls (watch_id, image_url) FROM stdin;
\.


--
-- TOC entry 4969 (class 0 OID 0)
-- Dependencies: 219
-- Name: credentials_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.credentials_seq', 801, true);


--
-- TOC entry 4970 (class 0 OID 0)
-- Dependencies: 220
-- Name: order_line_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.order_line_seq', 751, true);


--
-- TOC entry 4971 (class 0 OID 0)
-- Dependencies: 221
-- Name: orders_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.orders_seq', 651, true);


--
-- TOC entry 4972 (class 0 OID 0)
-- Dependencies: 231
-- Name: review_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.review_seq', 951, true);


--
-- TOC entry 4973 (class 0 OID 0)
-- Dependencies: 217
-- Name: user_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_seq', 1, false);


--
-- TOC entry 4974 (class 0 OID 0)
-- Dependencies: 222
-- Name: users_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_seq', 801, true);


--
-- TOC entry 4975 (class 0 OID 0)
-- Dependencies: 223
-- Name: watch_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.watch_seq', 601, true);


-- Completed on 2025-09-26 02:23:59

--
-- PostgreSQL database dump complete
--

