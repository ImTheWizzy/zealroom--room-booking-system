PGDMP         5                y            zealroom    14.1    14.1     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            �           1262    16394    zealroom    DATABASE     d   CREATE DATABASE zealroom WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'English_World.utf8';
    DROP DATABASE zealroom;
                postgres    false            �            1259    16411    booking    TABLE     �   CREATE TABLE public.booking (
    room_id integer NOT NULL,
    booking_uuid integer NOT NULL,
    user_uuid character varying NOT NULL,
    is_booked boolean NOT NULL,
    check_in date NOT NULL,
    check_out date NOT NULL
);
    DROP TABLE public.booking;
       public         heap    postgres    false            �            1259    16415    rooms    TABLE     �   CREATE TABLE public.rooms (
    capacity integer NOT NULL,
    room_uuid integer NOT NULL,
    room_description character varying(255) NOT NULL,
    room_number character varying(255) NOT NULL
);
    DROP TABLE public.rooms;
       public         heap    postgres    false            �            1259    16404    users    TABLE     I  CREATE TABLE public.users (
    user_uuid character varying(255) NOT NULL,
    first_name character varying(255) NOT NULL,
    last_name character varying(255) NOT NULL,
    is_admin boolean NOT NULL,
    session_token character varying(255),
    email character varying(255) NOT NULL,
    pwd character varying(255) NOT NULL
);
    DROP TABLE public.users;
       public         heap    postgres    false            �          0    16411    booking 
   TABLE DATA           c   COPY public.booking (room_id, booking_uuid, user_uuid, is_booked, check_in, check_out) FROM stdin;
    public          postgres    false    210   �       �          0    16415    rooms 
   TABLE DATA           S   COPY public.rooms (capacity, room_uuid, room_description, room_number) FROM stdin;
    public          postgres    false    211   
       �          0    16404    users 
   TABLE DATA           f   COPY public.users (user_uuid, first_name, last_name, is_admin, session_token, email, pwd) FROM stdin;
    public          postgres    false    209   D       f           2606    16432    booking booking_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public.booking
    ADD CONSTRAINT booking_pkey PRIMARY KEY (booking_uuid);
 >   ALTER TABLE ONLY public.booking DROP CONSTRAINT booking_pkey;
       public            postgres    false    210            h           2606    16425    rooms rooms_pkey 
   CONSTRAINT     U   ALTER TABLE ONLY public.rooms
    ADD CONSTRAINT rooms_pkey PRIMARY KEY (room_uuid);
 :   ALTER TABLE ONLY public.rooms DROP CONSTRAINT rooms_pkey;
       public            postgres    false    211            d           2606    16410    users users_pkey 
   CONSTRAINT     U   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_uuid);
 :   ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
       public            postgres    false    209            i           2606    16426    booking fk_rooms    FK CONSTRAINT     v   ALTER TABLE ONLY public.booking
    ADD CONSTRAINT fk_rooms FOREIGN KEY (room_id) REFERENCES public.rooms(room_uuid);
 :   ALTER TABLE ONLY public.booking DROP CONSTRAINT fk_rooms;
       public          postgres    false    210    3176    211            j           2606    16435    booking fk_users    FK CONSTRAINT     �   ALTER TABLE ONLY public.booking
    ADD CONSTRAINT fk_users FOREIGN KEY (user_uuid) REFERENCES public.users(user_uuid) NOT VALID;
 :   ALTER TABLE ONLY public.booking DROP CONSTRAINT fk_users;
       public          postgres    false    210    209    3172            �      x������ � �      �   *   x�340�4��p���Q	�Qp�O,J����U0����� ���      �      x������ � �     